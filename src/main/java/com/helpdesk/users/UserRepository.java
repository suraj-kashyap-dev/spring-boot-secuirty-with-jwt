package com.helpdesk.users;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public List<User> findAllByOrderByCreatedAtDesc();
    public User findByEmail(String email);
    public User findByProxyId(String proxyId);
    public User findByVerificationCode(String verificationCode);
    public User findByEmailAndVerificationCode(String email, String verificationCode);
    public User findByEmailAndProxyId(String email, String proxyId);
    public User findByEmailAndProxyIdAndVerificationCode(String email, String proxyId, String verificationCode);
    public User findByEmailAndEnabled(String email, boolean enabled);
    public User findByEmailAndProxyIdAndEnabled(String email, String proxyId, boolean enabled);
}
