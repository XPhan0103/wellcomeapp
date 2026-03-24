package com.example.wellcomeapp.repository;

import com.example.wellcomeapp.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Assignment a JOIN FETCH a.subject WHERE a.className = :className ORDER BY a.dueDate ASC")
    List<Assignment> findByClassNameWithSubject(@org.springframework.data.repository.query.Param("className") String className);
}
