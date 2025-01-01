package com.helpdesk.auth;

import java.util.HashMap;

import org.springframework.security.core.userdetails.UserDetails;

import com.helpdesk.responses.ApiResponse;
import com.helpdesk.users.User;

public interface AuthService {
    public ApiResponse<HashMap<String, Object>> login(AuthDTO authenticationDto);

    public ApiResponse<User> me(UserDetails userDetails);
}
