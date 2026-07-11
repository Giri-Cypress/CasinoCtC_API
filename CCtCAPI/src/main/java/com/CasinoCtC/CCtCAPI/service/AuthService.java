package com.CasinoCtC.CCtCAPI.service;

import com.CasinoCtC.CCtCAPI.model.LoginResponse;

public interface AuthService {
    LoginResponse login(String userName, String password);
    LoginResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
}
