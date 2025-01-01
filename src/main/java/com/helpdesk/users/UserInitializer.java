package com.helpdesk.users;

import com.helpdesk.roles.Role;
import com.helpdesk.roles.RoleRepository;
import com.helpdesk.userinstances.UserInstance;
import com.helpdesk.userinstances.UserInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

public class UserInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserInstanceRepository userInstanceRepository;

    @PostConstruct
    public void initializeAdminUser() {
        User hasUser = this.userRepository.findByEmail("admin@example.com");

        if (hasUser == null) {
            User user = new User();

            user.setEmail("admin@example.com");
            user.setFirstName("Example");
            user.setLastName("Admin");
            user.setEnabled(true);
            user.setTimezone("UTC");
            user.setPassword("adminPassword123");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            Role role = this.roleRepository.findByCode("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("Admin role not found"));

            UserInstance userInstance = new UserInstance();
            userInstance.setUser(user);
            userInstance.setSource("Admin Panel");
            userInstance.setSkypeId("admin_skype");
            userInstance.setContactNumber("1234567890");
            userInstance.setDesignation("Administrator");
            userInstance.setSignature("Best Regards, Admin");
            userInstance.setProfileImagePath("/path/to/default/image");
            userInstance.setCreatedAt(LocalDateTime.now());
            userInstance.setUpdatedAt(LocalDateTime.now());
            userInstance.setActive(true);
            userInstance.setVerified(true);
            userInstance.setStarred(true);
            userInstance.setTicketAccessLevel("ALL");
            userInstance.setRole(role);

            userInstanceRepository.save(userInstance);

            user.setUserInstances(List.of(userInstance));

            userRepository.save(user);
        }
    }
}
