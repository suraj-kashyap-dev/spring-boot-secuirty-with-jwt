package com.helpdesk.auth;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import com.helpdesk.responses.ApiResponse;
import com.helpdesk.users.User;

public interface AuthService {
    ResponseEntity<ApiResponse<HashMap<String, Object>>> login(AuthDTO authenticationDto);
    ResponseEntity<ApiResponse<User>> me(UserDetails userDetails);
}
