package com.project.backend.controllers;

import com.project.backend.models.Student;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.payment.IPaymentService;
import com.project.backend.services.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;
    private final StudentService studentService; // To fetch student details from the token

    // Create a payment
    @PostMapping("/create")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> createPayment(
            @RequestParam(defaultValue = "0") int a4Count,
            @RequestParam(defaultValue = "0") int a3Count,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {

        // Extract token from the Authorization header
        String token = authorizationHeader.substring(7);

        // Fetch the student details using the token
        Student student = studentService.getDetailFromToken(token);

        // Use the student's ID to create the payment
        ResponseObject response = paymentService.createPayment(student.getStudentId(), a4Count, a3Count, "MoMo",
                "pay with MoMo");
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
    @PreAuthorize("hasAnyRole('ADMIN', 'SPSO')")
    public ResponseEntity<ResponseObject> findPaymentByStudentId(@PathVariable Integer studentId) {
        ResponseObject response = paymentService.findPaymentByStudentId(studentId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Find payment by student using token
    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> findPaymentByStudentToken(
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        // Extract token from the Authorization header
        String token = authorizationHeader.substring(7);

        // Fetch the student details using the token
        Student student = studentService.getDetailFromToken(token);

        // Use the student's ID to fetch payments
        ResponseObject response = paymentService.findPaymentByStudentId(student.getStudentId());
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
