package com.helpdesk.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("proxy_id")
    private String proxyId;

    @JsonProperty("password")
    private String password;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("enabled")
    private boolean enabled;

    @JsonProperty("verification_code")
    private String verificationCode;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("time_format")
    private String timeFormat;
}
