package com.CasinoCtC.CCtCAPI.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CasinoCtC.CCtCAPI.dto.LoginRequest;
import com.CasinoCtC.CCtCAPI.model.LoginResponse;
import com.CasinoCtC.CCtCAPI.model.RefreshTokenRequest;
import com.CasinoCtC.CCtCAPI.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        System.out.println("Username received: [" + request.getUserName() + "]");
        return authService.login(request.getUserName(), request.getPassword());
    }

    @PostMapping("/refresh")
    public LoginResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request.getRefreshToken());
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
    }
}
