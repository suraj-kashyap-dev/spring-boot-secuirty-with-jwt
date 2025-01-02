package com.helpdesk.console;

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

        try {
            boolean rolesInitialized = initializeRoles();
            boolean adminInitialized = initializeAdmin();

            if (rolesInitialized || adminInitialized) {
                System.out.println("\n[INFO] System has been initialized successfully!");
            }
        } catch (Exception e) {
            System.err.println("\n[ERROR] System initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean initializeRoles() {
        // Define roles with ROLE_ prefix
        List<Role> roles = List.of(
            new Role("ROLE_ADMIN", "Account Owner"),
            new Role("ROLE_AGENT", "Agent"),
            new Role("ROLE_CUSTOMER", "Customer")
        );

        boolean rolesCreated = false;

        for (Role role : roles) {
            // Check if role exists without and with ROLE_ prefix
            String roleCode = role.getCode();
            String codeWithoutPrefix = roleCode.replace("ROLE_", "");
            
            Optional<Role> existingRole = roleRepository.findByCode(roleCode);
            if (existingRole.isEmpty()) {
                // Also check without ROLE_ prefix
                existingRole = roleRepository.findByCode(codeWithoutPrefix);
                if (existingRole.isPresent()) {
                    // Update existing role to include ROLE_ prefix
                    Role updatedRole = existingRole.get();
                    updatedRole.setCode(roleCode);
                    roleRepository.save(updatedRole);
                    System.out.println("[INFO] Role " + codeWithoutPrefix + " updated to " + roleCode);
                    rolesCreated = true;
                } else {
                    // Create new role
                    roleRepository.save(role);
                    System.out.println("[INFO] Role " + roleCode + " created.");
                    rolesCreated = true;
                }
            }
        }

        if (!rolesCreated) {
            System.out.println("[INFO] All roles are already initialized.");
        }

        return rolesCreated;
    }

    private boolean initializeAdmin() {
        if (userRepository.findByEmail(email) != null) {
            System.out.println("[INFO] Admin user already exists.");
            return false;
        }

        // Find admin role with ROLE_ prefix
        Role adminRole = roleRepository.findByCode("ROLE_ADMIN")
            .orElseThrow(() -> new RuntimeException("[ERROR] Admin role not found"));

        User admin = new User();
        admin.setEmail(email);
        admin.setPassword(bCryptPasswordEncoder.encode(password));
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEnabled(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        UserInstance adminInstance = new UserInstance();
        adminInstance.setUser(admin);
        adminInstance.setSource("website");
        adminInstance.setCreatedAt(LocalDateTime.now());
        adminInstance.setUpdatedAt(LocalDateTime.now());
        adminInstance.setActive(true);
        adminInstance.setVerified(true);
        adminInstance.setRole(adminRole);

        Set<UserInstance> userInstances = new HashSet<>();
        userInstances.add(adminInstance);
        admin.setUserInstances(userInstances);

        try {
            userRepository.save(admin);
            System.out.println("[INFO] Admin user created successfully.");
            return true;
        } catch (Exception e) {
            throw new RuntimeException("[ERROR] Failed to create admin user: " + e.getMessage(), e);
        }
    }
}