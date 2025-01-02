package com.helpdesk.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.helpdesk.userinstances.UserInstance;

import java.time.LocalDateTime;
import java.util.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Email should be valid")
    @Column(length = 191, unique = true, nullable = false)
    private String email;

    @NotNull(message = "Password should not be null")
    @Size(min = 8, message = "Password should be at least 8 characters")
    @Column(length = 191, nullable = false)
    private String password;

    @NotEmpty(message = "First name should not be empty")
    @Column(length = 191, nullable = false)
    private String firstName;

    @Column(length = 191)
    private String lastName;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(length = 191, unique = true)
    private String verificationCode;

    @Column
    private LocalDateTime lastOtpGeneratedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<UserInstance> userInstances = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        UserInstance activeInstance = getActiveUserInstance();

        if (activeInstance != null && activeInstance.getRole() != null) {
            String roleCode = activeInstance.getRole().getCode();
            
            if (! roleCode.startsWith("ROLE_")) {
                roleCode = "ROLE_" + roleCode;
            }
            
            authorities.add(new SimpleGrantedAuthority(roleCode));

            System.out.println("Assigned authority: " + roleCode);
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public UserInstance getActiveUserInstance() {
        return userInstances.stream()
            .filter(UserInstance::isActive)
            .findFirst()
            .orElse(null);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}