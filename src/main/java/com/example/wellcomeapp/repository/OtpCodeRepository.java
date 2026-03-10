package com.example.wellcomeapp.repository;

import com.example.wellcomeapp.model.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByPhoneNumberAndIsUsedFalseOrderByExpiresAtDesc(String phoneNumber);
}
