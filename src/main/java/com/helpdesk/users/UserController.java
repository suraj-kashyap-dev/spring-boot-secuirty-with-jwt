package com.helpdesk.users;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.helpdesk.responses.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> index() {
        return userService.index();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> show(@PathVariable Long id) {
        return userService.show(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> store(@RequestBody @Valid UserDTO userDto) {
        return userService.store(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> update(@PathVariable Long id, @RequestBody @Valid UserDTO userDto) {
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> destroy(@PathVariable Long id) {
        return userService.destroy(id);
    }
}