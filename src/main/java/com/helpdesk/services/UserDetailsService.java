package com.helpdesk.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.helpdesk.users.User;
import com.helpdesk.users.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(email);

        if (user == null) {
            System.out.println("User Not Found");

            throw new UsernameNotFoundException("User not found");
        }

        System.out.println("User Found " + user.toString());

        return user;
    }
}
