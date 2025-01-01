package com.helpdesk.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helpdesk.userinstances.UserInstance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("proxy_id")
    private String proxyId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("is_enabled")
    private boolean enabled;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("time_format")
    private String timeformat;

    @JsonProperty("password")
    private String password;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("user_instances")
    private List<UserInstance> userInstances;

    @JsonProperty("granted_roles")
    private List<String> grantedRoles;

    @JsonProperty("active_instance")
    private UserInstance activeInstance;
}
