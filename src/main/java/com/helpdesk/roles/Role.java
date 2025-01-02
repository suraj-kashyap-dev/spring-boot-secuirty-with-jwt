package com.helpdesk.roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.helpdesk.userinstances.UserInstance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true, nullable = false)
    private String code;

    @Column(length = 100)
    private String description;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserInstance> userInstances = new HashSet<>();

    public Role(String code, String description) {
        this.code = code;
        this.description = description;
    }

    
    public String getCode() {
        return code != null && !code.startsWith("ROLE_") ? "ROLE_" + code : code;
    }

    public void setCode(String code) {
        this.code = code.startsWith("ROLE_") ? code : "ROLE_" + code;
    }
}
