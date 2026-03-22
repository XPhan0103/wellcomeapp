package com.example.wellcomeapp.config;

import com.example.wellcomeapp.model.*;
import com.example.wellcomeapp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository, RoleRepository roleRepository,
            StudentRepository studentRepository, SubjectRepository subjectRepository,
            GradeRepository gradeRepository, ScheduleRepository scheduleRepository,
            AssignmentRepository assignmentRepository, NotificationRepository notificationRepository,
            AttendanceRepository attendanceRepository,
            TuitionPaymentRepository tuitionPaymentRepository,
            LeaveRequestRepository leaveRequestRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // ── ROLES ──────────────────────────────────────────────────────────
            Role parentRole = roleRepository.findByName("ROLE_PARENT").orElseGet(() -> {
                Role r = new Role(); r.setName("ROLE_PARENT"); r.setDescription("Phụ huynh học sinh");
                return roleRepository.save(r);
            });
            Role studentRole = roleRepository.findByName("ROLE_STUDENT").orElseGet(() -> {
                Role r = new Role(); r.setName("ROLE_STUDENT"); r.setDescription("Học sinh");
                return roleRepository.save(r);
            });

            // ── STUDENT ────────────────────────────────────────────────────────
            Student studentA;
            if (studentRepository.count() == 0) {
                studentA = new Student();
                studentA.setFullName("Nguyễn Văn A");
                studentA.setStudentCode("HS2025001");
                studentA.setClassName("10A1");
                studentA.setSchoolYear("2025-2026");
                studentA.setDateOfBirth(LocalDate.of(2009, 3, 15));
                studentA.setAddress("123 Nguyễn Trãi, Quận 1, TP.HCM");
                studentA = studentRepository.save(studentA);
            } else {
                studentA = studentRepository.findAll().get(0);
                // Patch missing fields
                if (studentA.getDateOfBirth() == null) {
                    studentA.setDateOfBirth(LocalDate.of(2009, 3, 15));
                    studentA.setAddress("123 Nguyễn Trãi, Quận 1, TP.HCM");
                    studentA = studentRepository.save(studentA);
                }
            }

            // ── PARENT USER ────────────────────────────────────────────────────
            Optional<User> userOpt = userRepository.findByPhoneNumber("0987654321");
            User parent;
            if (userOpt.isEmpty()) {
                parent = new User("0987654321", passwordEncoder.encode("123456"), "Phụ huynh Nguyễn Văn A");
                parent.addRole(parentRole);
                parent.addStudent(studentA);
                userRepository.save(parent);
            } else {
                parent = userOpt.get();
                parent.setPassword(passwordEncoder.encode("123456")); // Force update to BCrypt
                parent.addRole(parentRole);
                parent.addStudent(studentA);
                userRepository.save(parent);
            }

            // ── STUDENT USER (students can also login) ─────────────────────────────
            Optional<User> studentUserOpt = userRepository.findByPhoneNumber("0912345678");
            if (studentUserOpt.isEmpty()) {
                final Student fStudentRef = studentA;
                User studentUser = new User("0912345678", passwordEncoder.encode("123456"), "Nguyễn Văn A (Học sinh)");
                studentUser.addRole(studentRole);
                studentUser.addStudent(fStudentRef);
                userRepository.save(studentUser);
            } else {
                User studentUser = studentUserOpt.get();
                studentUser.setPassword(passwordEncoder.encode("123456")); // Force update to BCrypt
                userRepository.save(studentUser);
            }

            // ── SUBJECTS + GRADES + SCHEDULES + ASSIGNMENTS + NOTIFICATIONS ───
            if (subjectRepository.count() == 0) {
                Subject math = subjectRepository.save(newSubject("Toán"));
                Subject eng  = subjectRepository.save(newSubject("Tiếng Anh"));
                Subject lit  = subjectRepository.save(newSubject("Ngữ Văn"));
                Subject phys = subjectRepository.save(newSubject("Vật Lý"));
                Subject chem = subjectRepository.save(newSubject("Hóa Học"));

                // Grades – HK1 with semester + weight
                saveGradeEx(gradeRepository, studentA, math,  8.5, "15 phút",  "HK1", 1);
                saveGradeEx(gradeRepository, studentA, math,  9.0, "Giữa kỳ",  "HK1", 2);
                saveGradeEx(gradeRepository, studentA, math,  8.0, "Cuối kỳ",   "HK1", 3);
                saveGradeEx(gradeRepository, studentA, eng,   9.0, "Miệng",      "HK1", 1);
                saveGradeEx(gradeRepository, studentA, eng,   8.5, "1 tiết",    "HK1", 2);
                saveGradeEx(gradeRepository, studentA, eng,   9.5, "Cuối kỳ",   "HK1", 3);
                saveGradeEx(gradeRepository, studentA, lit,   7.5, "1 tiết",    "HK1", 2);
                saveGradeEx(gradeRepository, studentA, lit,   7.0, "Giữa kỳ",  "HK1", 2);
                saveGradeEx(gradeRepository, studentA, lit,   8.0, "Cuối kỳ",   "HK1", 3);
                saveGradeEx(gradeRepository, studentA, phys,  8.0, "15 phút",  "HK1", 1);
                saveGradeEx(gradeRepository, studentA, phys,  7.5, "Giữa kỳ",  "HK1", 2);
                saveGradeEx(gradeRepository, studentA, phys,  8.5, "Cuối kỳ",   "HK1", 3);
                saveGradeEx(gradeRepository, studentA, chem,  7.0, "Miệng",      "HK1", 1);
                saveGradeEx(gradeRepository, studentA, chem,  6.5, "1 tiết",    "HK1", 2);
                saveGradeEx(gradeRepository, studentA, chem,  7.5, "Cuối kỳ",   "HK1", 3);

                // Assignments
                saveAssignment(assignmentRepository, "BTVN Toán - Chương 2",  "Làm bài tập trang 32",    math, "10A1", LocalDateTime.now().withHour(20).withMinute(0));
                saveAssignment(assignmentRepository, "Đọc hiểu Văn",           "Soạn bài Chí Phèo",       lit,  "10A1", LocalDateTime.now().plusDays(2));
                saveAssignment(assignmentRepository, "Quiz Anh Unit 3",         "Làm quiz online Teams",   eng,  "10A1", LocalDateTime.now().plusDays(4));
                saveAssignment(assignmentRepository, "Bài tập Vật Lý ch.5",    "Giải bài 5.1–5.10",       phys, "10A1", LocalDateTime.now().plusDays(1));

                System.out.println("====== MOCK DATA (Phase 1) ĐÃ ĐƯỢC TẠO ======");
            }

            // ── FORCE RE-SEED SCHEDULES TO ENSURE VARIED DATA ───────────────
            scheduleRepository.deleteAll();
            Subject math = subjectRepository.findByName("Toán").orElseGet(() -> subjectRepository.save(newSubject("Toán")));
            Subject eng  = subjectRepository.findByName("Tiếng Anh").orElseGet(() -> subjectRepository.save(newSubject("Tiếng Anh")));
            Subject lit  = subjectRepository.findByName("Ngữ Văn").orElseGet(() -> subjectRepository.save(newSubject("Ngữ Văn")));
            Subject phys = subjectRepository.findByName("Vật Lý").orElseGet(() -> subjectRepository.save(newSubject("Vật Lý")));
            Subject chem = subjectRepository.findByName("Hóa Học").orElseGet(() -> subjectRepository.save(newSubject("Hóa Học")));

            // Thứ 2
            saveSchedule(scheduleRepository, "10A1", math,  "A203", LocalTime.of(7,30),  LocalTime.of(9,0),  2);
            saveSchedule(scheduleRepository, "10A1", lit,   "B105", LocalTime.of(9,15),  LocalTime.of(10,45), 2);
            saveSchedule(scheduleRepository, "10A1", eng,   "C301", LocalTime.of(13,30), LocalTime.of(15,0),  2);
            saveSchedule(scheduleRepository, "10A1", phys,  "D202", LocalTime.of(15,15), LocalTime.of(16,45), 2);
            
            // Thứ 3
            saveSchedule(scheduleRepository, "10A1", chem,  "A203", LocalTime.of(7,30),  LocalTime.of(9,0),  3);
            saveSchedule(scheduleRepository, "10A1", math,  "B105", LocalTime.of(9,15),  LocalTime.of(10,45), 3);
            saveSchedule(scheduleRepository, "10A1", phys,  "C301", LocalTime.of(13,30), LocalTime.of(15,0),  3);
            saveSchedule(scheduleRepository, "10A1", eng,   "D202", LocalTime.of(15,15), LocalTime.of(16,45), 3);
            
            // Thứ 4
            saveSchedule(scheduleRepository, "10A1", lit,   "A203", LocalTime.of(7,30),  LocalTime.of(9,0),  4);
            saveSchedule(scheduleRepository, "10A1", chem,  "B105", LocalTime.of(9,15),  LocalTime.of(10,45), 4);
            saveSchedule(scheduleRepository, "10A1", math,  "C301", LocalTime.of(13,30), LocalTime.of(15,0),  4);
            saveSchedule(scheduleRepository, "10A1", phys,  "D202", LocalTime.of(15,15), LocalTime.of(16,45), 4);
            
            // Thứ 5
            saveSchedule(scheduleRepository, "10A1", eng,   "A203", LocalTime.of(7,30),  LocalTime.of(9,0),  5);
            saveSchedule(scheduleRepository, "10A1", phys,  "B105", LocalTime.of(9,15),  LocalTime.of(10,45), 5);
            saveSchedule(scheduleRepository, "10A1", chem,  "C301", LocalTime.of(13,30), LocalTime.of(15,0),  5);
            saveSchedule(scheduleRepository, "10A1", lit,   "D202", LocalTime.of(15,15), LocalTime.of(16,45), 5);

            // Thứ 6
            saveSchedule(scheduleRepository, "10A1", math,  "A203", LocalTime.of(7,30),  LocalTime.of(9,0),  6);
            saveSchedule(scheduleRepository, "10A1", eng,   "B105", LocalTime.of(9,15),  LocalTime.of(10,45), 6);
            saveSchedule(scheduleRepository, "10A1", lit,   "C301", LocalTime.of(13,30), LocalTime.of(15,0),  6);
            saveSchedule(scheduleRepository, "10A1", chem,  "D202", LocalTime.of(15,15), LocalTime.of(16,45), 6);

            // ── NOTIFICATIONS (seeded independently so they survive Phase 1 skip) ────
            if (notificationRepository.count() == 0) {
                saveNotifEx(notificationRepository, "Lịch kiểm tra giữa kỳ",
                        "Lịch kiểm tra giữa kỳ sẽ được cập nhật vào tuần sau. Các em chú ý theo dõi.",
                        "HỌC_TẬP", false);
                saveNotifEx(notificationRepository, "Bài tập sắp đến hạn",
                        "Bạn có 1 bài tập Toán sắp đến hạn nộp – vui lòng nộp đúng hạn hôm nay.",
                        "HỌC_TẬP", false);
                saveNotifEx(notificationRepository, "Nhắc mang thẻ học sinh",
                        "Nhớ mang thẻ học sinh khi vào cổng trường. Bảo vệ sẽ kiểm tra từ ngày 21/03.",
                        "SỰ_KIỆN", true);
                saveNotifEx(notificationRepository, "Học phí tháng 3 sắp đến hạn",
                        "Học phí tháng 3 sắp đến hạn – vui lòng đóng trước ngày 25/03/2026.",
                        "HỌC_PHÍ", false);
                saveNotifEx(notificationRepository, "Ngày hội thể thao",
                        "Ngày hội thể thao trường diễn ra ngày 28/03. Học sinh cần đăng ký tham gia trước ngày 22/03.",
                        "SỰ_KIỆN", false);
                saveNotifEx(notificationRepository, "Nghỉ lễ 30/4 – 1/5",
                        "Học sinh được nghỉ từ 28/4 đến 2/5. Trường sẽ điều chỉnh lịch học bù vào tuần sau.",
                        "SỰ_KIỆN", true);
                saveNotifEx(notificationRepository, "Bảo hiểm y tế học sinh",
                        "Vui lòng nộp phí bảo hiểm y tế học sinh trước ngày 10/04/2026.",
                        "HỌC_PHÍ", false);
                saveNotifEx(notificationRepository, "Kỳ thi thử THPT",
                        "Lịch thi thử THPT Quốc gia sẽ diễn ra từ 15-17/05. Học sinh chú ý ôn tập.",
                        "HỌC_TẬP", false);
                saveNotifEx(notificationRepository, "Cảnh báo thời tiết xấu",
                        "Dự báo mưa lớn ngày mai. Phụ huynh chú ý đưa đón học sinh an toàn.",
                        "SỰ_KIỆN", false);
                saveNotifEx(notificationRepository, "Học sinh xuất sắc tháng 2",
                        "Bạn Nguyễn Văn A được bình chọn là Học sinh xuất sắc tháng 2/2026. Chúc mừng!",
                        "SỰ_KIỆN", true);
                System.out.println("====== MOCK NOTIFICATIONS (Phase 3) SEEDED ======");
            }

            // ── ATTENDANCE (chuyên cần) ────────────────────────────────────────
            if (attendanceRepository.count() == 0) {
                LocalDate today = LocalDate.now();
                // Seed ~4 weeks of attendance
                for (int i = 27; i >= 0; i--) {
                    LocalDate d = today.minusDays(i);
                    int dow = d.getDayOfWeek().getValue(); // 1=Mon … 7=Sun
                    if (dow >= 6) continue; // skip weekend
                    Attendance.Status status = Attendance.Status.PRESENT;
                    String note = "";
                    if (i == 10) { status = Attendance.Status.ABSENT;  note = "Ốm sốt"; }
                    if (i == 17) { status = Attendance.Status.LATE;    note = "Đến muộn 15 phút"; }
                    if (i == 22) { status = Attendance.Status.ABSENT;  note = "Xin phép có việc gia đình"; }
                    Attendance att = new Attendance();
                    att.setStudent(studentA);
                    att.setAttendanceDate(d);
                    att.setStatus(status);
                    att.setNote(note);
                    attendanceRepository.save(att);
                }
                System.out.println("====== MOCK ATTENDANCE SEEDED ======");
            }

            // ── TUITION PAYMENTS ────────────────────────────────────────────────
            if (tuitionPaymentRepository.count() == 0) {
                final Student fStudentA = studentA;
                saveTuition(tuitionPaymentRepository, fStudentA, "Học phí tháng 1/2026",     3_500_000, TuitionPayment.Status.PAID,   LocalDate.of(2026, 1, 20), LocalDate.of(2026, 1, 18));
                saveTuition(tuitionPaymentRepository, fStudentA, "Học phí tháng 2/2026",     3_500_000, TuitionPayment.Status.PAID,   LocalDate.of(2026, 2, 20), LocalDate.of(2026, 2, 15));
                saveTuition(tuitionPaymentRepository, fStudentA, "Học phí tháng 3/2026",     3_500_000, TuitionPayment.Status.UNPAID, LocalDate.of(2026, 3, 25), null);
                saveTuition(tuitionPaymentRepository, fStudentA, "Phí hoạt động ngoại khóa",   500_000, TuitionPayment.Status.UNPAID, LocalDate.of(2026, 3, 30), null);
                saveTuition(tuitionPaymentRepository, fStudentA, "Bảo hiểm y tế học sinh",     800_000, TuitionPayment.Status.PAID,   LocalDate.of(2026, 1, 15), LocalDate.of(2026, 1, 10));
                System.out.println("====== MOCK TUITION SEEDED ======");
            }

            // ── LEAVE REQUESTS ─────────────────────────────────────────────────
            if (leaveRequestRepository.count() == 0) {
                final Student fStudentA = studentA;
                saveLeaveReq(leaveRequestRepository, fStudentA, "Ốm sốt, cần nghỉ để điều trị", LocalDate.now().minusDays(10), LeaveRequest.Status.APPROVED, LocalDateTime.now().minusDays(11));
                saveLeaveReq(leaveRequestRepository, fStudentA, "Có việc gia đình đột xuất",    LocalDate.now().minusDays(22), LeaveRequest.Status.APPROVED, LocalDateTime.now().minusDays(23));
                saveLeaveReq(leaveRequestRepository, fStudentA, "Khám bệnh định kỳ",            LocalDate.now().plusDays(5),   LeaveRequest.Status.PENDING,  LocalDateTime.now().minusDays(1));
                System.out.println("====== MOCK LEAVE REQUESTS SEEDED ======");
            }
        };
    }

    // ── Helper methods ──────────────────────────────────────────────────────────

    private Subject newSubject(String name) {
        Subject s = new Subject(); s.setName(name); return s;
    }

    private void saveGrade(GradeRepository repo, Student s, Subject subj, double score, String type) {
        Grade g = new Grade(); g.setStudent(s); g.setSubject(subj); g.setScore(score); g.setType(type);
        repo.save(g);
    }

    private void saveGradeEx(GradeRepository repo, Student s, Subject subj, double score,
                             String type, String semester, int weight) {
        Grade g = new Grade();
        g.setStudent(s); g.setSubject(subj); g.setScore(score);
        g.setType(type); g.setSemester(semester); g.setWeight(weight);
        repo.save(g);
    }

    private void saveSchedule(ScheduleRepository repo, String cls, Subject subj, String room,
                              LocalTime start, LocalTime end, int dow) {
        Schedule s = new Schedule();
        s.setClassName(cls); s.setSubject(subj); s.setRoom("Phòng " + room);
        s.setStartTime(start); s.setEndTime(end); s.setDayOfWeek(dow);
        repo.save(s);
    }

    private void saveAssignment(AssignmentRepository repo, String title, String desc,
                                Subject subj, String cls, LocalDateTime due) {
        Assignment a = new Assignment();
        a.setTitle(title); a.setDescription(desc); a.setSubject(subj);
        a.setClassName(cls); a.setDueDate(due);
        repo.save(a);
    }

    private void saveNotification(NotificationRepository repo, String title, String content) {
        Notification n = new Notification(); n.setTitle(title); n.setContent(content);
        repo.save(n);
    }

    private void saveNotifEx(NotificationRepository repo, String title, String content,
                             String category, boolean isRead) {
        Notification n = new Notification();
        n.setTitle(title);
        n.setContent(content);
        n.setCategory(category);
        n.setIsRead(isRead);
        repo.save(n);
    }

    private void saveTuition(TuitionPaymentRepository repo, Student student,
                             String desc, long amount, TuitionPayment.Status status,
                             LocalDate dueDate, LocalDate paidAt) {
        TuitionPayment t = new TuitionPayment();
        t.setStudent(student);
        t.setDescription(desc);
        t.setAmount(BigDecimal.valueOf(amount));
        t.setStatus(status);
        t.setDueDate(dueDate);
        t.setPaidAt(paidAt);
        repo.save(t);
    }

    private void saveLeaveReq(LeaveRequestRepository repo, Student student,
                              String reason, LocalDate leaveDate,
                              LeaveRequest.Status status, LocalDateTime createdAt) {
        LeaveRequest r = new LeaveRequest();
        r.setStudent(student);
        r.setReason(reason);
        r.setLeaveDate(leaveDate);
        r.setStatus(status);
        r.setParentName("Phụ huynh Nguyễn Văn A");
        r.setCreatedAt(createdAt);
        repo.save(r);
    }
}
