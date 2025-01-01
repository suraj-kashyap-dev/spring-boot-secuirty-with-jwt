package com.helpdesk.users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.helpdesk.exceptions.resources.ResourceCreationException;
import com.helpdesk.exceptions.resources.ResourceDeletionException;
import com.helpdesk.exceptions.resources.ResourceNotFoundException;
import com.helpdesk.exceptions.resources.ResourceUpdateException;
import com.helpdesk.responses.ApiResponse;
import com.helpdesk.roles.Role;
import com.helpdesk.roles.RoleRepository;
import com.helpdesk.userinstances.UserInstance;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

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
    public ApiResponse<User> store(UserDTO userDto) {
        try {
            User user = this.modelMapper.map(userDto, User.class);

            UserInstance adminInstance = new UserInstance();

            Role customer = roleRepository.findByCode("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("[ERROR] Role not found"));

            adminInstance.setUser(user);
            adminInstance.setSource("website");
            adminInstance.setCreatedAt(LocalDateTime.now());
            adminInstance.setUpdatedAt(LocalDateTime.now());
            adminInstance.setActive(true);
            adminInstance.setVerified(true);
            adminInstance.setRole(customer);

            user.setActiveInstance(adminInstance);
            user.setUserInstances(List.of(adminInstance));
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
    public ApiResponse<User> update(Long id, UserDTO userDto) {
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
