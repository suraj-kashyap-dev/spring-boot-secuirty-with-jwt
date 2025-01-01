package com.helpdesk.users;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.helpdesk.exceptions.resources.ResourceCreationException;
import com.helpdesk.exceptions.resources.ResourceDeletionException;
import com.helpdesk.exceptions.resources.ResourceNotFoundException;
import com.helpdesk.exceptions.resources.ResourceUpdateException;
import com.helpdesk.responses.ApiResponse;
import com.helpdesk.services.JwtService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Autowired
    public UserServiceImpl(
        UserRepository userRepository,
        ModelMapper modelMapper,
        AuthenticationManager authenticationManager,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager; 
        this.jwtService = jwtService;
    }

    @Override
    public ApiResponse<List<User>> index() {
        try {
            List<User> users = this.userRepository.findAllByOrderByCreatedAtDesc();
            return ApiResponse.success("Users retrieved successfully", users);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving users", e);
        }
    }

    @Override
    public ApiResponse<User> show(Long id) {
        try {
            Optional<User> users = this.userRepository.findById(id);

            return users
                .map(user -> ApiResponse.success("User retrieved successfully", user))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException("Error retrieving user");
        }
    }

    @Override
    public ApiResponse<User> store(UserDto userDto) {
        try {
            User user = this.modelMapper.map(userDto, User.class);

            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            
            return ApiResponse.success(
                "User created successfully", 
                this.userRepository.save(user)
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceCreationException("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse login(UserDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = this.userRepository.findByEmail(userDetails.getUsername());

            if (user == null) {
                throw new BadCredentialsException("Invalid email or password");
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("token", this.jwtService.generateToken(user.getEmail()));
            data.put("user", user);

            return ApiResponse.success("Login successful", data);
        }

        throw new BadCredentialsException("Invalid email or password");
    }


    @Override
    public ApiResponse<User> update(Long id, UserDto userDto) {
        try {
            Optional<User> userOpt = this.userRepository.findById(id);

            if (userOpt.isEmpty()) {
                throw new ResourceNotFoundException("User not found");
            }

            User existingUser = userOpt.get();

            return ApiResponse.success(
                "User updated successfully", 
                this.userRepository.save(existingUser)
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();

            throw new ResourceUpdateException("Error updating User: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Void> destroy(Long id) {
        try {
            if (! this.userRepository.existsById(id)) {
                throw new ResourceNotFoundException("User not found");
            }

            this.userRepository.deleteById(id);
            return ApiResponse.success("User deleted successfully", null);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceDeletionException("Error deleting user");
        }
    }
}
