package com.helpdesk.console;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.helpdesk.users.User;
import com.helpdesk.users.UserRepository;
import com.helpdesk.roles.Role;
import com.helpdesk.roles.RoleRepository;
import com.helpdesk.userinstances.UserInstance;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class InstallHelpDesk implements CommandLineRunner {
    @Value("${helpdesk.admin.password}")
    private String password;

    @Value("${helpdesk.admin.email}")
    private String email;

    @Value("${helpdesk.admin.first-name}")
    private String firstName;

    @Value("${helpdesk.admin.last-name}")
    private String lastName;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @Override
    public void run(String... args) {
        System.out.println("\n=== Welcome to HelpDesk System Initialization ===");

        boolean rolesInitialized = initializeRoles();
        boolean adminInitialized = initializeAdmin();

        if (rolesInitialized || adminInitialized) {
            System.out.println("\n[INFO] System has been initialized successfully!");
        }
    }

    private boolean initializeRoles() {
        List<Role> roles = List.of(
            new Role("ADMIN", "Account Owner"),
            new Role("AGENT", "Agent"),
            new Role("CUSTOMER", "Customer")
        );

        boolean rolesCreated = false;

        for (Role role : roles) {
            if (roleRepository.findByCode(role.getCode()).isEmpty()) {
                roleRepository.save(role);
                rolesCreated = true;
                System.out.println("[INFO] Role " + role.getCode() + " created.");
            }
        }

        if (!rolesCreated) {
            System.out.println("[INFO] All roles are already initialized.");
        }

        return rolesCreated;
    }

    private boolean initializeAdmin() {
        User adminUser = userRepository.findByEmail("admin@example.com");

        if (adminUser != null) {
            return false;
        }

        User admin = new User();
        admin.setEmail(email);
        admin.setPassword(bCryptPasswordEncoder.encode(password));
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEnabled(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        Role adminRole = roleRepository.findByCode("ADMIN")
            .orElseThrow(() -> new RuntimeException("[ERROR] Admin role not found"));

        Hibernate.initialize(adminRole);

        UserInstance adminInstance = new UserInstance();
        adminInstance.setUser(admin);
        adminInstance.setSource("website");
        adminInstance.setCreatedAt(LocalDateTime.now());
        adminInstance.setUpdatedAt(LocalDateTime.now());
        adminInstance.setActive(true);
        adminInstance.setVerified(true);
        adminInstance.setRole(adminRole);

        admin.setUserInstances(Set.of(adminInstance));

        userRepository.save(admin);

        return true;
    }
}
