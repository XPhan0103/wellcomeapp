package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.TuitionPayment;
import com.example.wellcomeapp.repository.TuitionPaymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class TuitionController {

    private final TuitionPaymentRepository tuitionRepo;

    public TuitionController(TuitionPaymentRepository tuitionRepo) {
        this.tuitionRepo = tuitionRepo;
    }

    @GetMapping("/{studentId}/tuition")
    public ResponseEntity<?> getTuition(@PathVariable Long studentId) {
        List<TuitionPayment> payments = tuitionRepo.findByStudentIdOrderByDueDateDesc(studentId);

        List<Map<String, Object>> items = payments.stream().map(t -> Map.<String, Object>of(
                "id",          t.getId(),
                "description", t.getDescription(),
                "amount",      t.getAmount(),
                "status",      t.getStatus().name(),
                "dueDate",     t.getDueDate() != null ? t.getDueDate().toString() : "",
                "paidAt",      t.getPaidAt() != null ? t.getPaidAt().toString() : ""
        )).collect(Collectors.toList());

        double totalPaid = payments.stream()
                .filter(t -> t.getStatus() == TuitionPayment.Status.PAID)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();
        double totalUnpaid = payments.stream()
                .filter(t -> t.getStatus() != TuitionPayment.Status.PAID)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        return ResponseEntity.ok(Map.of(
                "totalPaid",   totalPaid,
                "totalUnpaid", totalUnpaid,
                "items",       items
        ));
    }
}
