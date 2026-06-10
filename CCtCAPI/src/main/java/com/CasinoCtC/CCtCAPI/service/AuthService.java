package com.CasinoCtC.CCtCAPI.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.dao.LocationDAO;
import com.CasinoCtC.CCtCAPI.dao.MenuDAO;
import com.CasinoCtC.CCtCAPI.dao.RefreshTokenDAO;
import com.CasinoCtC.CCtCAPI.dao.RoleDAO;
import com.CasinoCtC.CCtCAPI.dao.UserDAO;

import com.CasinoCtC.CCtCAPI.model.Location;
import com.CasinoCtC.CCtCAPI.model.LoginResponse;
import com.CasinoCtC.CCtCAPI.model.Menu;
import com.CasinoCtC.CCtCAPI.model.RefreshToken;
import com.CasinoCtC.CCtCAPI.model.User;

import com.CasinoCtC.CCtCAPI.security.HashUtil;
import com.CasinoCtC.CCtCAPI.security.JwtUtil;

@Service
public class AuthService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final MenuDAO menuDAO;
    private final LocationDAO locationDAO;
    private final RefreshTokenDAO refreshTokenDAO;

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final HashUtil hashUtil;

    private final AuditService auditService;

    // ✅ Constructor Injection
    public AuthService(
            UserDAO userDAO,
            RoleDAO roleDAO,
            MenuDAO menuDAO,
            LocationDAO locationDAO,
            RefreshTokenDAO refreshTokenDAO,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            HashUtil hashUtil,
            AuditService auditService) {

        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.menuDAO = menuDAO;
        this.locationDAO = locationDAO;
        this.refreshTokenDAO = refreshTokenDAO;

        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.hashUtil = hashUtil;

        this.auditService = auditService;
    }

    /**
     * ✅ LOGIN
     */
    public LoginResponse login(
            String username,
            String password) {

         // ✅ Find User
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> {

                    auditService.logEvent(
                            username,
                            "LOGIN_FAILED",
                            "User not found",
                            "/api/auth/login",
                            "POST",
                            "localhost",
                            "FAILED"
                    );

                    return new RuntimeException(
                            "User not found"
                    );
                });

        // ✅ Validate User Status
        if (user.getStatus() == null
                || user.getStatus() != 1) {

            auditService.logEvent(
                    username,
                    "LOGIN_FAILED",
                    "User inactive",
                    "/api/auth/login",
                    "POST",
                    "localhost",
                    "FAILED"
            );

            throw new RuntimeException(
                    "User inactive"
            );
        }

        // ✅ Validate Password
        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            auditService.logEvent(
                    username,
                    "LOGIN_FAILED",
                    "Invalid password",
                    "/api/auth/login",
                    "POST",
                    "localhost",
                    "FAILED"
            );

            throw new RuntimeException(
                    "Invalid password"
            );
        }

        // ✅ Load Roles
        List<String> roles =
                roleDAO.findRolesByUserId(
                        user.getUserId()
                );

        // ✅ Load Menus
        List<Menu> menus =
                menuDAO.findMenusByUserId(
                        user.getUserId()
                );

        // ✅ Load Location
        Location location =
                locationDAO.findById(
                        user.getLocationId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Location not found"
                        )
                );

        // ✅ Generate Access Token
        String accessToken =
                jwtUtil.generateAccessToken(
                        user.getUserName(),
                        roles
                );

        // ✅ Generate Refresh Token
        String refreshToken =
                jwtUtil.generateRefreshToken(
                        user.getUserName()
                );

        // ✅ Hash Refresh Token
        String refreshTokenHash =
                hashUtil.sha256(refreshToken);

        // ✅ Save Refresh Token Hash
        refreshTokenDAO.save(
                user.getUserId(),
                refreshTokenHash,
                new Date(
                        System.currentTimeMillis()
                                + 1000L * 60 * 60 * 24 * 7
                )
        );

        // ✅ Audit Success Login
        auditService.logEvent(
                username,
                "LOGIN_SUCCESS",
                "User logged in successfully",
                "/api/auth/login",
                "POST",
                "localhost",
                "SUCCESS"
        );
 
        // ✅ Build Response
        LoginResponse response =
                new LoginResponse();

        response.setUsername(
                user.getUserName()
        );

        response.setRoles(roles);

        response.setMenus(menus);

        response.setLocationName(
                location.getLocationName()
        );

        response.setToken(accessToken);

        response.setRefreshToken(
                refreshToken
        );

        return response;
    }

    /**
     * ✅ REFRESH TOKEN WITH ROTATION
     */
    public LoginResponse refreshToken(
            String refreshToken) {

        // ✅ Validate JWT Structure
        if (!jwtUtil.validateToken(
                refreshToken)) {

            auditService.logEvent(
                    null,
                    "TOKEN_REFRESH_FAILED",
                    "Invalid refresh token",
                    "/api/auth/refresh",
                    "POST",
                    "localhost",
                    "FAILED"
            );

            throw new RuntimeException(
                    "Invalid refresh token"
            );
        }

        // ✅ Hash Incoming Token
        String oldTokenHash =
                hashUtil.sha256(refreshToken);

        // ✅ Validate Stored Token
        RefreshToken storedToken =
                refreshTokenDAO.findValidToken(
                        oldTokenHash
                )
                .orElseThrow(() -> {

                    auditService.logEvent(
                            null,
                            "TOKEN_REFRESH_FAILED",
                            "Refresh token revoked or expired",
                            "/api/auth/refresh",
                            "POST",
                            "localhost",
                            "FAILED"
                    );

                    return new RuntimeException(
                            "Refresh token revoked or expired"
                    );
                });

        // ✅ Extract Username
        String username =
                jwtUtil.extractUsername(
                        refreshToken
                );

        // ✅ Reload User
        User user = userDAO.findByUsername(
                username
        )
        .orElseThrow(() ->
                new RuntimeException(
                        "User not found"
                )
        );

        // ✅ Load Roles
        List<String> roles =
                roleDAO.findRolesByUserId(
                        user.getUserId()
                );

        // ✅ Revoke Old Token
        refreshTokenDAO.revokeToken(
                oldTokenHash
        );

        // ✅ Generate New Access Token
        String newAccessToken =
                jwtUtil.generateAccessToken(
                        username,
                        roles
                );

        // ✅ Generate New Refresh Token
        String newRefreshToken =
                jwtUtil.generateRefreshToken(
                        username
                );

        // ✅ Hash New Refresh Token
        String newRefreshTokenHash =
                hashUtil.sha256(
                        newRefreshToken
                );

        // ✅ Save New Refresh Token
        refreshTokenDAO.save(
                user.getUserId(),
                newRefreshTokenHash,
                new Date(
                        System.currentTimeMillis()
                                + 1000L * 60 * 60 * 24 * 7
                )
        );

        // ✅ Audit Refresh Success
        auditService.logEvent(
                username,
                "TOKEN_REFRESH",
                "Refresh token rotated successfully",
                "/api/auth/refresh",
                "POST",
                "localhost",
                "SUCCESS"
        );

        // ✅ Build Response
        LoginResponse response =
                new LoginResponse();

        response.setUsername(username);

        response.setRoles(roles);

        response.setToken(newAccessToken);

        response.setRefreshToken(
                newRefreshToken
        );

        return response;
    }

    /**
     * ✅ LOGOUT
     */
    public void logout(String refreshToken) {

        // ✅ Hash Token
        String tokenHash =
                hashUtil.sha256(refreshToken);

        // ✅ Revoke Token
        refreshTokenDAO.revokeToken(
                tokenHash
        );

        // ✅ Audit Logout
        auditService.logEvent(
                null,
                "LOGOUT",
                "User logged out",
                "/api/auth/logout",
                "POST",
                "localhost",
                "SUCCESS"
        );
    }
}