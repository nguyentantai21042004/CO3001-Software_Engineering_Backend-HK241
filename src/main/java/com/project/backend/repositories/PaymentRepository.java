package com.project.backend.repositories;

import com.project.backend.models.Payment;
import com.project.backend.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    // Find payments by the entire Student object
    List<Payment> findByStudent(Student student);
    Optional<Payment> findByOrderId(String orderId);

}
