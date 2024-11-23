package com.project.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Many-to-One relationship with Student
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @Min(value = 1, message = "Balance must be greater than 0")
    private Integer balance;

    @Min(value = 1, message = "Amount must be greater than 0")
    private Integer amount;

    @NotBlank
    private String method;

    @Column(name = "transaction_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime transactionDate;

    @NotBlank
    @Column(nullable = false, length = 15)
    private String status;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId; // Use UUID for uniqueness across orders

    @Column(name = "request_id", nullable = false, unique = true)
    private String requestId; // Use UUID for uniqueness across requests

    @PrePersist
    public void prePersist() {
        if (this.transactionDate == null) {
            this.transactionDate = LocalDateTime.now();
        }
    }
}
