package com.helpdesk;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
    public CsrfToken index(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }
}
