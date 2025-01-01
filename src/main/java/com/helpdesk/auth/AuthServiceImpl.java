package com.helpdesk.auth;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;
    
    @Override
    public ApiResponse<User> me(UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ApiResponse.error("User is not authenticated");
            }
    
        return ApiResponse.success("User retrieved successfully", (User) userDetails);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("Error retrieving user");
        }
    }

    public ApiResponse<HashMap<String, Object>> login(AuthDTO authenticationDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authenticationDto.getEmail(), authenticationDto.getPassword())
        );
    
        System.out.println(authenticationDto.toString());
    
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    
            User user = this.userRepository.findByEmail(userDetails.getUsername());
    
            if (user == null) {
                // Return a detailed error message when the user is not found
                HashMap<String, String> errors = new HashMap<>();
                errors.put("email", "Invalid email or password");
                return ApiResponse.error("Login failed", errors);
            }
    
            HashMap<String, Object> data = new HashMap<>();
            data.put("token", this.jwtService.generateToken(user.getEmail()));
            data.put("user", user);
    
            return ApiResponse.success("Login successful", data);
        }
    
        HashMap<String, String> errors = new HashMap<>();
        errors.put("credentials", "Invalid email or password");
        return ApiResponse.error("Login failed", errors);
    }
    
}
