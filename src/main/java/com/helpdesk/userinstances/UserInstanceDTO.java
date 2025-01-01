package com.helpdesk.userinstances;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helpdesk.roles.Role;
import com.helpdesk.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInstanceDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("source")
    private String source;

    @JsonProperty("skype_id")
    private String skypeId;

    @JsonProperty("contact_number")
    private String contactNumber;

    @JsonProperty("designation")
    private String designation;

    @JsonProperty("signature")
    private String signature;

    @JsonProperty("profile_image_path")
    private String profileImagePath;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("verified")
    private boolean verified;

    @JsonProperty("starred")
    private boolean starred;

    @JsonProperty("ticket_access_level")
    private String ticketAccessLevel;

    @JsonProperty("default_filtering")
    private Integer defaultFiltering;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("role_id")
    private Long roleId;

    // Convert DTO to Entity
    public UserInstance toEntity(User user, Role role) {
        UserInstance instance = new UserInstance();
        instance.setId(this.id);
        instance.setSource(this.source);
        instance.setSkypeId(this.skypeId);
        instance.setContactNumber(this.contactNumber);
        instance.setDesignation(this.designation);
        instance.setSignature(this.signature);
        instance.setProfileImagePath(this.profileImagePath);
        instance.setCreatedAt(this.createdAt);
        instance.setUpdatedAt(this.updatedAt);
        instance.setActive(this.active);
        instance.setVerified(this.verified);
        instance.setStarred(this.starred);
        instance.setTicketAccessLevel(this.ticketAccessLevel);
        instance.setDefaultFiltering(this.defaultFiltering);
        instance.setUser(user);
        instance.setRole(role);
        return instance;
    }

    // Convert Entity to DTO
    public static UserInstanceDTO fromEntity(UserInstance instance) {
        return new UserInstanceDTO(
            instance.getId(),
            instance.getSource(),
            instance.getSkypeId(),
            instance.getContactNumber(),
            instance.getDesignation(),
            instance.getSignature(),
            instance.getProfileImagePath(),
            instance.getCreatedAt(),
            instance.getUpdatedAt(),
            instance.isActive(),
            instance.isVerified(),
            instance.isStarred(),
            instance.getTicketAccessLevel(),
            instance.getDefaultFiltering(),
            instance.getUser() != null ? instance.getUser().getId() : null,
            instance.getRole() != null ? instance.getRole().getId() : null
        );
    }
}
