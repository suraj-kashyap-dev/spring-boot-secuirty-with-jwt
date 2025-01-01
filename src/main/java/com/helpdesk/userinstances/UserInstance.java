package com.helpdesk.userinstances;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.helpdesk.roles.Role;
import com.helpdesk.users.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_instances")
public class UserInstance {
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

    @Column(nullable = false)
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

    @Transient
    private Integer defaultFiltering;

    @JsonBackReference("user-userInstance")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonBackReference
    private Role role;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isActive() {
        return active;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isStarred() {
        return starred;
    }
}