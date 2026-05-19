package com.carhelper.service;

import com.carhelper.dto.AuthRequest;
import com.carhelper.model.User;
import com.carhelper.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank() || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Email and password are required.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        String username = request.getUsername() == null || request.getUsername().isBlank() ? request.getEmail() : request.getUsername();
        User newUser = new User(username, request.getEmail(), request.getPassword());
        return userRepository.save(newUser);
    }

    public User login(AuthRequest request) {
        User foundUser = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        if (!foundUser.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        return foundUser;
    }
}