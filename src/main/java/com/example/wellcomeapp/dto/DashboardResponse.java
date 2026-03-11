package com.example.wellcomeapp.dto;

import java.util.List;

public class DashboardResponse {
    private StudentInfo student;
    private List<ScheduleDTO> todaySchedules;
    private List<AssignmentDTO> pendingAssignments;

    public DashboardResponse() {}

    public StudentInfo getStudent() { return student; }
    public void setStudent(StudentInfo student) { this.student = student; }
    public List<ScheduleDTO> getTodaySchedules() { return todaySchedules; }
    public void setTodaySchedules(List<ScheduleDTO> todaySchedules) { this.todaySchedules = todaySchedules; }
    public List<AssignmentDTO> getPendingAssignments() { return pendingAssignments; }
    public void setPendingAssignments(List<AssignmentDTO> pendingAssignments) { this.pendingAssignments = pendingAssignments; }

    public static class StudentInfo {
        private Long id;
        private String name;
        private String className;
        private String schoolYear;

        public StudentInfo(Long id, String name, String className, String schoolYear) {
            this.id = id;
            this.name = name;
            this.className = className;
            this.schoolYear = schoolYear;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getClassName() { return className; }
        public String getSchoolYear() { return schoolYear; }
    }

    public static class ScheduleDTO {
        private String time;
        private String subject;
        private String room;

        public ScheduleDTO(String time, String subject, String room) {
            this.time = time;
            this.subject = subject;
            this.room = room;
        }

        public String getTime() { return time; }
        public String getSubject() { return subject; }
        public String getRoom() { return room; }
    }

    public static class AssignmentDTO {
        private String title;
        private String dueDate;
        private String description;

        public AssignmentDTO(String title, String dueDate, String description) {
            this.title = title;
            this.dueDate = dueDate;
            this.description = description;
        }

        public String getTitle() { return title; }
        public String getDueDate() { return dueDate; }
        public String getDescription() { return description; }
    }
}
