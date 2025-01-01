package com.helpdesk.roles;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.helpdesk.userinstances.UserInstance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "support_roles")
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 191, unique = true)
    private String code;

    @Column(length = 191)
    private String description;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<UserInstance> userInstances;

    public Role(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
