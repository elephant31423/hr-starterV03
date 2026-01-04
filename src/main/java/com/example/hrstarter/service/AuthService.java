package com.example.hrstarter.service;


import com.example.hrstarter.entity.User;

import java.util.List;
import java.util.Optional;

public interface AuthService {
    Optional<User> validateCredentials(String username, String rawPassword);
    String loginAndGenerateToken(User user); // 可選：直接回 token
    List<String> getUserPermissions(Long id);

    String refreshToken();
}
