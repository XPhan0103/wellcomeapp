package com.example.wellcomeapp.repository;

import com.example.wellcomeapp.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentIdOrderByAttendanceDateDesc(Long studentId);
}
