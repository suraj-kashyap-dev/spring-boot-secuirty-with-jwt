package com.helpdesk.users;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 191, unique = true, nullable = false)
    private String email;

    @Column(length = 191, unique = true)
    private String proxyId;

    @Column(length = 191)
    private String password;

    @Column(length = 191, nullable = false)
    private String firstName;

    @Column(length = 191)
    private String lastName;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(length = 191, unique = true)
    private String verificationCode;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProfile> userProfiles = new ArrayList<>();

    private String timezone;

    private String timeFormat;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
