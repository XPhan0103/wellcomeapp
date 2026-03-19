package com.example.wellcomeapp.repository;

import com.example.wellcomeapp.model.FeedbackMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackMessageRepository extends JpaRepository<FeedbackMessage, Long> {
    List<FeedbackMessage> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
