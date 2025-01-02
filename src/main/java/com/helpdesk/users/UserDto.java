package com.helpdesk.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helpdesk.userinstances.UserInstanceDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "First name should not be empty")
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private boolean enabled;

    private String timezone;

    private String timeformat;

    @NotNull(message = "Password should not be null")
    @Size(min = 8, message = "Password should be at least 8 characters")
    private String password;

    private MultipartFile profile;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<UserInstanceDTO> userInstances = new ArrayList<>();

    private UserInstanceDTO activeInstance;
}
