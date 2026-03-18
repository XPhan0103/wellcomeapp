package com.example.wellcomeapp.repository;

import com.example.wellcomeapp.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
