package com.example.wellcomeapp.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Timestamp createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Constructors
    public User() {
    }

    public User(String phoneNumber, String password, String fullName) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.fullName = fullName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // --- Student-specific fields ---
    @Column(name = "student_code", length = 50)
    private String studentCode;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(name = "school_year", length = 50)
    private String schoolYear;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 255)
    private String address;

    // --- Relationship ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private User parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<User> children = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    // --- New Getters and Setters ---
    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSchoolYear() { return schoolYear; }
    public void setSchoolYear(String schoolYear) { this.schoolYear = schoolYear; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public User getParent() { return parent; }
    public void setParent(User parent) { this.parent = parent; }

    public Set<User> getChildren() { return children; }
    public void setChildren(Set<User> children) { this.children = children; }

    public void addChild(User child) {
        this.children.add(child);
        child.setParent(this);
    }
}
