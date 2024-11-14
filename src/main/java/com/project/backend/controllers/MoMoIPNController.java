package com.project.backend.controllers;

import com.project.backend.models.Payment;
import com.project.backend.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/momo")
@RequiredArgsConstructor
public class MoMoIPNController {

    private final PaymentRepository paymentRepository;

    @PostMapping("/ipn")
    public ResponseEntity<String> handleMoMoIPN(@RequestBody Map<String, Object> payload) {
        try {
            String orderId = (String) payload.get("orderId");
            int resultCode = (int) payload.get("resultCode");

            // Find the payment by orderId
            Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
            if (paymentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found.");
            }

            Payment payment = paymentOpt.get();

            // Update payment status based on resultCode
            if (resultCode == 0) {
                // Payment was successful
                payment.setStatus("successful");
            } else {
                // Payment failed
                payment.setStatus("failed");
            }

            // Save the updated payment status
            paymentRepository.save(payment);

            return ResponseEntity.ok("IPN received and processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing IPN.");
        }
    }
}
