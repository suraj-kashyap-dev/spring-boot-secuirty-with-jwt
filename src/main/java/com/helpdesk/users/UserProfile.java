package com.helpdesk.users;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 191)
    private String source;

    @Column(length = 191)
    private String skypeId;

    @Column(length = 191)
    private String contactNumber;

    @Column(length = 191)
    private String designation;

    @Column(columnDefinition = "TEXT")
    private String signature;

    @Column(columnDefinition = "TEXT")
    private String profileImagePath;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean active = false;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private boolean starred = false;

    @Column(length = 32)
    private String ticketAccessLevel;

    private Integer defaultFiltering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}