package com.helpdesk.userinstances;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.helpdesk.roles.Role;
import com.helpdesk.users.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_instances")
public class UserInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source")
    private String source;

    @Column(name = "skype_id")
    private String skypeId;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "designation")
    private String designation;

    @Column(name = "signature")
    private String signature;

    @Column(name = "profile_image_path")
    private String profileImagePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "is_verified")
    private boolean verified;

    @Column(name = "is_starred")
    private boolean starred;

    @Column(name = "ticket_access_level")
    private String ticketAccessLevel;

    @Column(name = "default_filtering")
    private Integer defaultFiltering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnore
    private Role role;
}
