package com.motel.service;

import com.motel.domain.User;
import com.motel.dto.LoginRequest;
import com.motel.dto.LoginResponse;
import com.motel.repository.UserRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;

@Singleton
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new HttpStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return new LoginResponse(true, user.getUsername(), user.getFullName(), "Login successful");
    }
}
