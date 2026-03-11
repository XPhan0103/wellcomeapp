package com.example.wellcomeapp.repository;

import com.example.wellcomeapp.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByClassNameOrderByDueDateAsc(String className);
}
