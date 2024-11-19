package com.project.backend.controllers;

import com.project.backend.models.Payment;
import com.project.backend.repositories.PaymentRepository;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.payment.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;
    private final PaymentRepository paymentRepository; // Inject PaymentRepository to access payments

    // Create a payment
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('STUDENT'")
    public ResponseEntity<ResponseObject> createPayment(
            @RequestParam Integer studentId,
            @RequestParam(defaultValue = "0") int a4Count,
            @RequestParam(defaultValue = "0") int a3Count,
            @RequestParam String method) {

        ResponseObject response = paymentService.createPayment(studentId, a4Count, a3Count, "MoMo", "pay with MoMo");
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Find all payments
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SPSO')")
    public ResponseEntity<ResponseObject> findAllPayments() {
        ResponseObject response = paymentService.findAllPayments();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Find payment by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'SPSO')")
    public ResponseEntity<ResponseObject> findPaymentById(@PathVariable Integer id) {
        ResponseObject response = paymentService.findPaymentById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Find payment by Student ID
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'SPSO')")
    public ResponseEntity<ResponseObject> findPaymentByStudentId(@PathVariable Integer studentId) {
        ResponseObject response = paymentService.findPaymentByStudentId(studentId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
