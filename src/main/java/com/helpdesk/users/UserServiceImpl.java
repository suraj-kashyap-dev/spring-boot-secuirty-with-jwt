package com.helpdesk.users;

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

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

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
