package com.example.wellcomeapp.repository;

import com.example.wellcomeapp.model.TuitionPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TuitionPaymentRepository extends JpaRepository<TuitionPayment, Long> {
    List<TuitionPayment> findByStudentIdOrderByDueDateDesc(Long studentId);
}
