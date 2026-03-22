package com.example.wellcomeapp.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private Double score;

    /** Loại điểm: Miệng | 15 phút | 1 tiết | Giữa kỳ | Cuối kỳ */
    @Column(nullable = false)
    private String type;

    /** Học kỳ: HK1 | HK2 */
    @Column(name = "semester")
    private String semester = "HK1";

    /** Hệ số điểm: 1 = thường xuyên, 2 = giữa kỳ, 3 = cuối kỳ */
    @Column(name = "weight")
    private Integer weight = 1;

    @Column(name = "created_at", updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Timestamp createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

