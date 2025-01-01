package com.helpdesk.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.helpdesk.userinstances.UserInstance;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 191, unique = true, nullable = false)
    private String email;

    @Column(length = 191, unique = true, nullable = true)
    private String proxyId;

    @Column(length = 191, nullable = false)
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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    @JsonManagedReference
    private UserInstance activeInstance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonIgnore
    private List<UserInstance> userInstances = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public UserDTO toUserDTO() {
        return new UserDTO(
            this.id,
            this.email,
            this.proxyId,
            this.firstName,
            this.lastName,
            this.enabled,
            this.timezone,
            this.timeformat,
            this.password,
            this.createdAt,
            this.updatedAt,
            this.userInstances,
            this.getActiveUserInstance()
        );
    }

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
}
