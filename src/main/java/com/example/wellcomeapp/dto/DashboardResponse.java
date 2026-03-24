package com.example.wellcomeapp.dto;

import java.util.List;

public class DashboardResponse {
    private StudentInfo student;
    private List<ScheduleDTO> todaySchedules;
    private List<AssignmentDTO> pendingAssignments;
    private double overallGpa;
    private List<SubjectSummaryDTO> subjects;

    public DashboardResponse() {}

    public StudentInfo getStudent() { return student; }
    public void setStudent(StudentInfo student) { this.student = student; }
    public List<ScheduleDTO> getTodaySchedules() { return todaySchedules; }
    public void setTodaySchedules(List<ScheduleDTO> todaySchedules) { this.todaySchedules = todaySchedules; }
    public List<AssignmentDTO> getPendingAssignments() { return pendingAssignments; }
    public void setPendingAssignments(List<AssignmentDTO> pendingAssignments) { this.pendingAssignments = pendingAssignments; }
    public double getOverallGpa() { return overallGpa; }
    public void setOverallGpa(double overallGpa) { this.overallGpa = overallGpa; }
    public List<SubjectSummaryDTO> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectSummaryDTO> subjects) { this.subjects = subjects; }

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
        private String subject;
        private Long subjectId;
        private boolean completed;

        public AssignmentDTO(String title, String dueDate, String description, String subject, Long subjectId, boolean completed) {
            this.title = title;
            this.dueDate = dueDate;
            this.description = description;
            this.subject = subject;
            this.subjectId = subjectId;
            this.completed = completed;
        }

        public String getTitle() { return title; }
        public String getDueDate() { return dueDate; }
        public String getDescription() { return description; }
        public String getSubject() { return subject; }
        public Long getSubjectId() { return subjectId; }
        public boolean isCompleted() { return completed; }
    }

    public static class SubjectSummaryDTO {
        private String subject;
        private double gpa;
        private List<GradeDetailDTO> grades;

        public SubjectSummaryDTO(String subject, double gpa, List<GradeDetailDTO> grades) {
            this.subject = subject;
            this.gpa = gpa;
            this.grades = grades;
        }

        public String getSubject() { return subject; }
        public double getGpa() { return gpa; }
        public List<GradeDetailDTO> getGrades() { return grades; }
    }

    public static class GradeDetailDTO {
        private String type;
        private double score;
        private String semester;
        private int weight;

        public GradeDetailDTO(String type, double score, String semester, int weight) {
            this.type = type;
            this.score = score;
            this.semester = semester;
            this.weight = weight;
        }

        public String getType() { return type; }
        public double getScore() { return score; }
        public String getSemester() { return semester; }
        public int getWeight() { return weight; }
    }
}
