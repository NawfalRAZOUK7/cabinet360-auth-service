package com.cabinet360.auth.config;

import com.cabinet360.auth.entity.AdminUser;
import com.cabinet360.auth.enums.Role;
import com.cabinet360.auth.repository.AdminUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer {

    private final AdminUserRepository adminRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminInitializer(AdminUserRepository adminRepo, PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initAdmin() {
        if (!adminRepo.existsByEmail(adminEmail)) {
            AdminUser admin = new AdminUser();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFirstName("Admin");
            admin.setLastName("Root");
            admin.setRole(Role.ADMIN);
            admin.setConfirmed(true);
            admin.setActive(true);
            adminRepo.save(admin);
            System.out.println("✅ Admin user created: " + adminEmail);
        } else {
            System.out.println("ℹ️ Admin already exists: " + adminEmail);
        }
    }
}
