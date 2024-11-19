package com.project.backend.services.payment;

import com.project.backend.exceptions.DataNotFoundException;
import com.project.backend.models.Payment;
import com.project.backend.models.Student;
import com.project.backend.repositories.PaymentRepository;
import com.project.backend.repositories.StudentRepository;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.momo.MoMoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MoMoService moMoService;

    // Constants defined within the service class
    private static final int A4_BALANCES = 1; // Balance for A4 paper
    private static final int A3_BALANCES = 2; // Balance for A3 paper
    private static final int COST_PER_BALANCE = 500; // Cost per balance

    public ResponseObject createPayment(Integer studentId, int a4Count, int a3Count, String method, String orderInfo) {
        ResponseObject response = new ResponseObject();
        try {
            // Fetch the student based on studentId
            Optional<Student> studentOpt = studentRepository.findById(studentId.longValue());
            if (studentOpt.isEmpty()) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setMessage("Student not found.");
                return response;
            }

            Student student = studentOpt.get();

            int totalPoints = (a4Count * A4_BALANCES) + (a3Count * A3_BALANCES);

            if (totalPoints == 0) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setMessage("Total balances must be greater than zero.");
                return response;
            }
            if (totalPoints < 2) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setMessage(
                        "The request was denied because the transaction amount is less than the minimum allowed amount of 1000 VND");
                return response;
            }

            int amount = totalPoints * COST_PER_BALANCE;
            String orderId = UUID.randomUUID().toString();
            String requestId = UUID.randomUUID().toString();

            Payment payment = new Payment();
            payment.setStudent(student);
            payment.setBalance(totalPoints);
            payment.setAmount(amount);
            payment.setMethod(method);
            payment.setStatus("in progress");
            payment.setOrderId(orderId);
            payment.setRequestId(requestId);
            paymentRepository.save(payment);

            Map<String, Object> moMoResponse = moMoService.createMoMoPayment(orderId, requestId, String.valueOf(amount),
                    orderInfo);

            if (moMoResponse != null && "Thành công.".equals(moMoResponse.get("message"))) {
                // Payment initiation was successful, status will be updated via IPN
            } else {
                payment.setStatus("failed");
            }
            paymentRepository.save(payment);

            response.setStatus(HttpStatus.OK);
            response.setMessage("Payment processed successfully.");
            response.setData(moMoResponse);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage("Error processing payment: " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseObject findAllPayments() {
        ResponseObject response = new ResponseObject();
        try {
            List<Payment> payments = paymentRepository.findAll();
            response.setData(payments);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Fetched all payments successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseObject findPaymentById(Integer id) {
        ResponseObject response = new ResponseObject();
        try {
            Optional<Payment> payment = paymentRepository.findById(id);
            if (payment.isEmpty()) {
                throw new DataNotFoundException("Payment not found");
            }
            response.setData(payment.get());
            response.setStatus(HttpStatus.OK);
            response.setMessage("Payment found successfully");
        } catch (DataNotFoundException e) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseObject findPaymentByStudentId(Integer studentId) {
        ResponseObject response = new ResponseObject();
        try {
            // Fetch the student based on studentId
            Optional<Student> studentOpt = studentRepository.findById(studentId.longValue());
            if (studentOpt.isEmpty()) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setMessage("Student not found.");
                return response;
            }

            // Retrieve payments based on the student object
            List<Payment> payments = paymentRepository.findByStudent(studentOpt.get());
            response.setData(payments);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Fetched payments by student ID successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

}