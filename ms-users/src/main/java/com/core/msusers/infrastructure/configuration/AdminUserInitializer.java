package com.core.msusers.infrastructure.configuration;

import com.core.msusers.infrastructure.persistence.entity.UserEntity;
import com.core.msusers.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.enabled:true}")
    private boolean enabled;

    @Value("${app.seed.admin.name:Admin Principal}")
    private String adminName;

    @Value("${app.seed.admin.email:admin@transportes.local}")
    private String adminEmail;

    @Value("${app.seed.admin.password:Admin123!}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Admin seed disabled by configuration");
            return;
        }

        userRepository.findByUserEmail(adminEmail).ifPresentOrElse(existing -> {
            if (!"ADMIN".equalsIgnoreCase(existing.getUserRole())) {
                existing.setUserRole("ADMIN");
                userRepository.save(existing);
                log.info("Updated existing user role to ADMIN for {}", adminEmail);
            } else {
                log.info("Admin user already exists: {}", adminEmail);
            }
        }, () -> {
            UserEntity admin = new UserEntity();
            admin.setUserName(adminName);
            admin.setUserEmail(adminEmail);
            admin.setUserPassword(passwordEncoder.encode(adminPassword));
            admin.setUserRole("ADMIN");
            admin.setActive(true);
            userRepository.save(admin);
            log.info("Seeded default admin user: {}", adminEmail);
        });
    }
}
