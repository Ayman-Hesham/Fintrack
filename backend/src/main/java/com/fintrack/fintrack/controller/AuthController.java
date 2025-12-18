package com.fintrack.fintrack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fintrack.fintrack.dto.userDTO.AuthResponse;
import com.fintrack.fintrack.dto.userDTO.LoginUserRequest;
import com.fintrack.fintrack.dto.userDTO.RegisterUserRequest;
import com.fintrack.fintrack.dto.userDTO.UserResponse;
import com.fintrack.fintrack.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest req) {
        UserResponse res = authService.createUser(req);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginUserRequest req) {
        AuthResponse res = authService.loginUser(req);
        return ResponseEntity.ok(res);
    }
}
