package com.helpdesk.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.helpdesk.responses.ApiResponse;
import com.helpdesk.users.User;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    final private AuthService authenticationService;

    @Autowired
    public AuthController(AuthService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> me(@AuthenticationPrincipal UserDetails userDetails) {
        return this.authenticationService.me(userDetails);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<HashMap<String, Object>>> login(@RequestBody AuthDTO authenticationDto) {
        return this.authenticationService.login(authenticationDto);
    }
}
