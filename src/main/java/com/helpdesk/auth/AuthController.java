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
        ApiResponse<User> response = this.authenticationService.me(userDetails);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(@RequestBody AuthDTO authenticationDto) {
        ApiResponse<User> response = this.authenticationService.login(authenticationDto);

        return ResponseEntity.ok(response);
    }
}