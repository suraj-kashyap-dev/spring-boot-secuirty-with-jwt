package com.helpdesk.users;

import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.helpdesk.exceptions.resources.ResourceNotFoundException;
import com.helpdesk.responses.ApiResponse;
import com.helpdesk.roles.Role;
import com.helpdesk.roles.RoleRepository;
import com.helpdesk.userinstances.UserInstance;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(
        UserRepository userRepository,
        RoleRepository roleRepository, 
        ModelMapper modelMapper,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> index() {
        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
        return ApiResponse.ok("Users retrieved successfully", users);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<User>> show(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ApiResponse.ok("User retrieved successfully", user);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<User>> store(@Valid UserDTO userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role customerRole = roleRepository.findByCode("ROLE_CUSTOMER")
            .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        UserInstance userInstance = new UserInstance();
        userInstance.setUser(user);
        userInstance.setRole(customerRole);
        userInstance.setSource("website");
        userInstance.setActive(true);
        userInstance.setVerified(true);

        user.setUserInstances(Set.of(userInstance));
        User savedUser = userRepository.save(user);
        
        return ApiResponse.ok("User created successfully", savedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> update(Long id, @Valid UserDTO userDto) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        modelMapper.map(userDto, existingUser);
        
        if (userDto.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return ApiResponse.ok("User updated successfully", updatedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> destroy(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
        return ApiResponse.ok("User deleted successfully", null);
    }
}