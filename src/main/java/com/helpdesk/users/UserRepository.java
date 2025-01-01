package com.helpdesk.users;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public List<User> findAllByOrderByCreatedAtDesc();
    public User findByEmail(String email);
    public User findByVerificationCode(String verificationCode);
    public User findByEmailAndVerificationCode(String email, String verificationCode);
    public User findByEmailAndEnabled(String email, boolean enabled);
}
