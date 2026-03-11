package com.example.wellcomeapp.config;

import com.example.wellcomeapp.model.*;
import com.example.wellcomeapp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository,
                                   StudentRepository studentRepository, SubjectRepository subjectRepository,
                                   GradeRepository gradeRepository, ScheduleRepository scheduleRepository,
                                   AssignmentRepository assignmentRepository, NotificationRepository notificationRepository) {
        return args -> {
            // Create Role if not exists
            Role parentRole = roleRepository.findByName("ROLE_PARENT").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_PARENT");
                r.setDescription("Phụ huynh học sinh");
                return roleRepository.save(r);
            });

            // Create Student
            Student studentA = new Student();
            studentA.setFullName("Nguyễn Văn A");
            studentA.setStudentCode("HS2025001");
            studentA.setClassName("10A1");
            studentA.setSchoolYear("2025-2026");
            
            // Check if student exists
            if (studentRepository.count() == 0) {
                studentA = studentRepository.save(studentA);
            } else {
                studentA = studentRepository.findAll().get(0);
                studentA.setFullName("Nguyễn Văn A");
                studentA.setClassName("10A1");
                studentA.setSchoolYear("2025-2026");
                studentA = studentRepository.save(studentA);
            }

            // Create User (Parent)
            Optional<User> userOpt = userRepository.findByPhoneNumber("0987654321");
            User parent;
            if (userOpt.isEmpty()) {
                parent = new User("0987654321", "123456", "Phụ huynh Nguyễn Văn A");
                parent.addRole(parentRole);
                parent.addStudent(studentA);
                userRepository.save(parent);
            } else {
                parent = userOpt.get();
                parent.addRole(parentRole);
                parent.addStudent(studentA);
                userRepository.save(parent);
            }

            // Create Subjects and Mocks if subjects are empty
            if (subjectRepository.count() == 0) {
                Subject math = new Subject(); math.setName("Toán");
                Subject english = new Subject(); english.setName("Tiếng Anh");
                Subject lit = new Subject(); lit.setName("Ngữ Văn");
                
                math = subjectRepository.save(math);
                english = subjectRepository.save(english);
                lit = subjectRepository.save(lit);

                // Create Grades
                Grade g1 = new Grade(); g1.setStudent(studentA); g1.setSubject(math); g1.setScore(8.5); g1.setType("15 phút"); gradeRepository.save(g1);
                Grade g2 = new Grade(); g2.setStudent(studentA); g2.setSubject(english); g2.setScore(9.0); g2.setType("Miệng"); gradeRepository.save(g2);
                Grade g3 = new Grade(); g3.setStudent(studentA); g3.setSubject(lit); g3.setScore(7.5); g3.setType("1 tiết"); gradeRepository.save(g3);

                // Create Schedules (Schedule for Mon-Fri)
                for (int i = 2; i <= 6; i++) {
                    Schedule s1 = new Schedule(); s1.setClassName("10A1"); s1.setSubject(math); s1.setRoom("Phòng A203"); s1.setStartTime(LocalTime.of(7, 30)); s1.setEndTime(LocalTime.of(9, 0)); s1.setDayOfWeek(i); scheduleRepository.save(s1);
                    Schedule s2 = new Schedule(); s2.setClassName("10A1"); s2.setSubject(lit); s2.setRoom("Phòng B105"); s2.setStartTime(LocalTime.of(9, 15)); s2.setEndTime(LocalTime.of(10, 45)); s2.setDayOfWeek(i); scheduleRepository.save(s2);
                    Schedule s3 = new Schedule(); s3.setClassName("10A1"); s3.setSubject(english); s3.setRoom("Phòng C301"); s3.setStartTime(LocalTime.of(13, 30)); s3.setEndTime(LocalTime.of(15, 0)); s3.setDayOfWeek(i); scheduleRepository.save(s3);
                }

                // Create Assignments
                Assignment a1 = new Assignment(); a1.setTitle("BTVN Toán - Chương 2"); a1.setDescription("Làm bài tập trang 32"); a1.setSubject(math); a1.setClassName("10A1"); a1.setDueDate(LocalDateTime.now().withHour(20).withMinute(0)); assignmentRepository.save(a1);
                Assignment a2 = new Assignment(); a2.setTitle("Đọc hiểu Văn"); a2.setDescription("Soạn bài"); a2.setSubject(lit); a2.setClassName("10A1"); a2.setDueDate(LocalDateTime.now().plusDays(2)); assignmentRepository.save(a2);
                Assignment a3 = new Assignment(); a3.setTitle("Quiz Anh Unit 3"); a3.setDescription("Làm quiz online"); a3.setSubject(english); a3.setClassName("10A1"); a3.setDueDate(LocalDateTime.now().plusDays(4)); assignmentRepository.save(a3);

                // Create Notifications
                Notification n1 = new Notification(); n1.setTitle("Thông báo"); n1.setContent("Lịch kiểm tra giữa kỳ sẽ cập nhật vào tuần sau."); notificationRepository.save(n1);
                Notification n2 = new Notification(); n2.setTitle("Nhắc nhở"); n2.setContent("Bạn có 1 bài tập sắp đến hạn."); notificationRepository.save(n2);
                Notification n3 = new Notification(); n3.setTitle("Nhà trường"); n3.setContent("Nhớ mang thẻ học sinh khi vào cổng."); notificationRepository.save(n3);

                System.out.println("====== DỮ LIỆU MOCK ĐÃ ĐƯỢC TẠO THÀNH CÔNG ======");
            }
        };
    }
}
