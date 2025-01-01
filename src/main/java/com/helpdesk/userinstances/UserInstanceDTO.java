package com.helpdesk.userinstances;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}