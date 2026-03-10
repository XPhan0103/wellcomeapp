package com.example.wellcomeapp.config;

import com.example.wellcomeapp.model.Role;
import com.example.wellcomeapp.model.User;
import com.example.wellcomeapp.repository.RoleRepository;
import com.example.wellcomeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {

        // 1. Create Roles if they don't exist
        Role parentRole = createRoleIfNotFound("ROLE_PARENT", "Phụ huynh học sinh");
        createRoleIfNotFound("ROLE_TEACHER", "Giáo viên");
        createRoleIfNotFound("ROLE_ADMIN", "Quản trị viên");
        createRoleIfNotFound("ROLE_STUDENT", "Học sinh");

        // 2. Create a test Parent User if not exists
        String testPhone = "0987654321";
        Optional<User> existingUser = userRepository.findByPhoneNumber(testPhone);

        if (existingUser.isEmpty()) {
            User testParent = new User();
            testParent.setPhoneNumber(testPhone);
            testParent.setPassword("123456"); // Plain password for simple testing
            testParent.setFullName("Phụ huynh Nguyễn Văn A");
            testParent.setIsActive(true);
            testParent.addRole(parentRole);

            userRepository.save(testParent);
            System.out.println("====== SEEDER: Đã tạo tài khoản Phụ huynh test thành công! ======");
            System.out.println("SĐT: 0987654321");
            System.out.println("Pass: 123456");
            System.out.println("================================================================");
        } else {
            System.out.println("====== SEEDER: Tài khoản Phụ huynh test (0987654321) đã tồn tại trong DB ======");
        }
    }

    private Role createRoleIfNotFound(String name, String description) {
        Optional<Role> roleOpt = roleRepository.findByName(name);
        if (roleOpt.isEmpty()) {
            Role role = new Role(name, description);
            return roleRepository.save(role);
        }
        return roleOpt.get();
    }
}
