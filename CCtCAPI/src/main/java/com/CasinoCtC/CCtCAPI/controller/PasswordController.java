package com.CasinoCtC.CCtCAPI.controller;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordController {

    private final PasswordEncoder passwordEncoder;

    public PasswordController(
            PasswordEncoder passwordEncoder) {

        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/encode/{password}")
    public String encodePassword(
            @PathVariable String password) {

        return passwordEncoder.encode(password);
    }
}
