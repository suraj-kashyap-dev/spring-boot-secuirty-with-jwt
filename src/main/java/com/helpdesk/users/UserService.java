package com.helpdesk.users;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.helpdesk.responses.ApiResponse;

public interface UserService {
    public ApiResponse<List<User>> index();
    public ApiResponse<User> show(Long id);
    public ApiResponse<User> store(UserDTO userDto);
    public ApiResponse<User> update(Long id, UserDTO userDto);
    public ApiResponse<Void> destroy(Long id);
}
