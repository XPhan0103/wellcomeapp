package com.example.wellcomeapp.repository;

import com.example.wellcomeapp.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT g FROM Grade g JOIN FETCH g.subject WHERE g.student.id = :studentId ORDER BY g.createdAt DESC")
    List<Grade> findByStudentIdWithSubject(Long studentId);
}
