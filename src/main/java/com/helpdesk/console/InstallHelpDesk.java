package com.helpdesk.console;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.regex.Pattern;

@Component
public class InstallHelpDesk implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    private final Scanner scanner = new Scanner(System.in);
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    public void run(String... args) {
        System.out.println("\n=== Welcome to HelpDesk System Initialization ===");

        boolean rolesInitialized = initializeRoles();
        boolean adminInitialized = initializeAdmin();

        if (rolesInitialized || adminInitialized) {
            System.out.println("\n[INFO] System has been initialized successfully!");
        } else {
            System.out.println("\n[INFO] HelpDesk system is already fully set up.");
        }
    }

    private boolean initializeRoles() {
        List<Role> roles = Arrays.asList(
            new Role(null, "ROLE_SUPER_ADMIN", "Account Owner"),
            new Role(null, "ROLE_ADMIN", "Administrator"),
            new Role(null, "ROLE_AGENT", "Agent"),
            new Role(null, "ROLE_CUSTOMER", "Customer")
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
            System.out.println("[INFO] Admin account already exists.");
            return false;
        }

        System.out.println("\n=== Create Admin Account ===");
        String email = promptEmail();
        String password = promptPassword();
        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        User admin = new User();
        admin.setEmail(email);
        admin.setPassword(bCryptPasswordEncoder.encode(password));
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEnabled(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        Role superAdminRole = roleRepository.findByCode("ROLE_SUPER_ADMIN")
            .orElseThrow(() -> new RuntimeException("[ERROR] Super Admin role not found"));

        UserInstance adminInstance = new UserInstance();
        adminInstance.setUser(admin);
        adminInstance.setSource("website");
        adminInstance.setCreatedAt(LocalDateTime.now());
        adminInstance.setUpdatedAt(LocalDateTime.now());
        adminInstance.setActive(true);
        adminInstance.setVerified(true);
        adminInstance.setRole(superAdminRole);

        admin.setUserInstances(List.of(adminInstance));
        userRepository.save(admin);

        System.out.println("\n[INFO] Admin account created successfully.");
        return true;
    }

    private String promptEmail() {
        while (true) {
            System.out.print("Admin Email: ");
            String email = scanner.nextLine().trim();

            if (EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            }

            System.out.println("[ERROR] Invalid email format. Please try again.");
        }
    }

    private String promptPassword() {
        while (true) {
            System.out.print("Password (min 8 characters): ");
            String password = scanner.nextLine();

            if (password.length() < 8) {
                System.out.println("[ERROR] Password must be at least 8 characters long.");
                continue;
            }

            System.out.print("Confirm Password: ");
            String confirmPassword = scanner.nextLine();

            if (password.equals(confirmPassword)) {
                return password;
            }

            System.out.println("[ERROR] Passwords don't match. Please try again.");
        }
    }
}
