package com.example.wellcomeapp.repository;

import com.example.wellcomeapp.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
