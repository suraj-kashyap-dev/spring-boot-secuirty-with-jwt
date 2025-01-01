package com.helpdesk.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.helpdesk.responses.ApiResponse;
import com.helpdesk.services.JwtService;
import com.helpdesk.users.User;
import com.helpdesk.users.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, 
                         UserRepository userRepository, 
                         JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }
    
    @Override
    public ResponseEntity<ApiResponse<User>> me(UserDetails userDetails) {
        if (userDetails == null) {
            return ApiResponse.error("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }
        return ApiResponse.ok("User retrieved successfully", (User) userDetails);
    }

    @Override
    public ResponseEntity<ApiResponse<HashMap<String, Object>>> login(AuthDTO authenticationDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authenticationDto.getEmail(), 
                    authenticationDto.getPassword()
                )
            );

            if (!authentication.isAuthenticated()) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("credentials", "Invalid credentials");
                return ApiResponse.error("Login failed", errors, HttpStatus.UNAUTHORIZED);
            }

            User user = userRepository.findByEmail(authenticationDto.getEmail());
            if (user == null) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("email", "User not found");
                return ApiResponse.error("Login failed", errors, HttpStatus.NOT_FOUND);
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("token", jwtService.generateToken(user.getEmail()));
            data.put("user", user);

            return ApiResponse.ok("Login successful", data);
        } catch (Exception e) {
            System.out.println("Authentication error: " + e.getMessage());
            return ApiResponse.error("Authentication failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}