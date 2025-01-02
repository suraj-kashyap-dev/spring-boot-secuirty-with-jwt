package com.helpdesk.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String index() {
        return "Hello, Admin!";
    }
}
