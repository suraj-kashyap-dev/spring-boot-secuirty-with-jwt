package com.helpdesk.users;

import java.util.List;

import com.helpdesk.responses.ApiResponse;

public interface UserService {
    public ApiResponse<List<User>> index();
    public ApiResponse<User> show(Long id);
    public ApiResponse<User> store(UserDto userDto);
    public ApiResponse<User> update(Long id, UserDto userDto);
    public ApiResponse<Void> destroy(Long id);
    public ApiResponse<User> login(UserDto userDto);
}
