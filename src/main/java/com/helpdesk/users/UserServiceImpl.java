package com.helpdesk.users;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.helpdesk.exceptions.resources.ResourceNotFoundException;
import com.helpdesk.responses.ApiResponse;
import com.helpdesk.roles.Role;
import com.helpdesk.roles.RoleRepository;
import com.helpdesk.services.FileService;
import com.helpdesk.userinstances.UserInstance;
import com.helpdesk.userinstances.UserInstanceRepository;

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
    private final FileService fileService;
    private final UserInstanceRepository userInstanceRepository;

    @Autowired
    public UserServiceImpl(
        UserRepository userRepository,
        RoleRepository roleRepository, 
        ModelMapper modelMapper,
        PasswordEncoder passwordEncoder,
        FileService fileService,
        UserInstanceRepository userInstanceRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
        this.userInstanceRepository = userInstanceRepository;
    }

    @Override
    public ResponseEntity<ApiResponse<List<User>>> index() {
        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
        return ApiResponse.ok("Users retrieved successfully", users);
    }

    @Override
    public ResponseEntity<ApiResponse<User>> show(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ApiResponse.ok("User retrieved successfully", user);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<User>> store(@Valid UserDTO userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ResourceNotFoundException("User with this email already exists");
        }

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
        user.setEnabled(true);
        User savedUser = userRepository.save(user);

        try {
            String filePath = this.fileService.uploadFile(
                userDto.getProfile(), 
                "users/" + savedUser.getId()
            );
            
            userInstance.setProfileImagePath(filePath);
            
            this.userInstanceRepository.save(userInstance);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file: " + e.getMessage());
        }

        return ApiResponse.ok("User created successfully", savedUser);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<User>> update(@Valid UserDTO userDto, Long id) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        modelMapper.map(userDto, existingUser);

        if (userDto.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (userDto.getProfile() != null && !userDto.getProfile().isEmpty()) {
            try {
                String filePath = this.fileService.uploadFile(
                    userDto.getProfile(),
                    "users/" + existingUser.getId()
                );

                UserInstance userInstance = existingUser.getActiveUserInstance();

                userInstance.setProfileImagePath(filePath);

                this.userInstanceRepository.save(userInstance);

            } catch (IOException e) {
                throw new RuntimeException("Error uploading file: " + e.getMessage());
            }
        } else if (userDto.getProfile() == null) {
            UserInstance userInstance = existingUser.getActiveUserInstance();

            if (userInstance.getProfileImagePath() != null) {
                try {
                    this.fileService.deleteFile(userInstance.getProfileImagePath(), "users/" + existingUser.getId());

                    userInstance.setProfileImagePath(null);

                    this.userInstanceRepository.save(userInstance);
                } catch (IOException e) {
                    throw new RuntimeException("Error removing profile image: " + e.getMessage());
                }
            }
        }

        User updatedUser = userRepository.save(existingUser);

        return ApiResponse.ok("User updated successfully", updatedUser);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Void>> destroy(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }

        this.userRepository.deleteById(id);
        return ApiResponse.ok("User deleted successfully", null);
    }
}
