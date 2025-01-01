package com.helpdesk.users;

import org.springframework.http.ResponseEntity;
import com.helpdesk.responses.ApiResponse;
import java.util.List;

public interface UserService {
    ResponseEntity<ApiResponse<List<User>>> index();
    ResponseEntity<ApiResponse<User>> show(Long id);
    ResponseEntity<ApiResponse<User>> store(UserDTO userDto);
    ResponseEntity<ApiResponse<User>> update(Long id, UserDTO userDto);
    ResponseEntity<ApiResponse<Void>> destroy(Long id);
}