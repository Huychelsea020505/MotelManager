package com.motel.controller;

import com.motel.dto.LoginRequest;
import com.motel.dto.LoginResponse;
import com.motel.service.AuthService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.validation.Valid;

@Controller("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Post("/login")
    public LoginResponse login(@Body @Valid LoginRequest request) {
        return authService.login(request);
    }
}
