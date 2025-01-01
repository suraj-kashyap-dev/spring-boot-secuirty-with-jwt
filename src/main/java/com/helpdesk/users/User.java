package com.helpdesk.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(length = 191, unique = true)
    private String email;

    @Column(length = 191, unique = true)
    private String proxyId;

    @Column(length = 191)
    private String password;

    @Column(length = 191, nullable = false)
    private String firstName;

    @Column(length = 191, nullable = true)
    private String lastName;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(length = 191, unique = true)
    private String verificationCode;

    @Column(length = 191)
    private String timezone;

    @Column(length = 191)
    private String timeformat;

    @Column
    private LocalDateTime lastOtpGeneratedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    private UserInstance activeInstance;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserInstance> userInstances = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    //     List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    //     for (String role : grantedRoles) {
    //         authorities.add(new SimpleGrantedAuthority(role));
    //     }

    //     return authorities;
    // }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
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

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public UserInstance getActiveUserInstance() {
        if (activeInstance == null && !userInstances.isEmpty()) {
            activeInstance = userInstances.stream()
                .filter(UserInstance::isActive)
                .findFirst()
                .orElse(userInstances.get(0));
        }

        return activeInstance;
    }
}