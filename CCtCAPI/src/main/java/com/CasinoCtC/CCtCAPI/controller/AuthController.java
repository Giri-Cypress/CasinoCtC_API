package com.CasinoCtC.CCtCAPI.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.CasinoCtC.CCtCAPI.model.LoginResponse;
import com.CasinoCtC.CCtCAPI.model.RefreshTokenRequest;
import com.CasinoCtC.CCtCAPI.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService) {

        this.authService = authService;
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody Map<String, String> request) {

        String username =
                request.get("username");

        String password =
                request.get("password");

        return authService.login(
                username,
                password
        );
    }

    // ✅ REFRESH TOKEN
    @PostMapping("/refresh")
    public LoginResponse refreshToken(
            @RequestBody RefreshTokenRequest request) {

        return authService.refreshToken(
                request.getRefreshToken()
        );
    }

    // ✅ LOGOUT
    @PostMapping("/logout")
    public void logout(
            @RequestBody RefreshTokenRequest request) {

        authService.logout(
                request.getRefreshToken()
        );
    }
}