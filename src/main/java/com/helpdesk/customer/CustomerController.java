package com.helpdesk.customer;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public String index() {
        return "Hello, Customer or Admin!";
    }
}
