package com.CasinoCtC.CCtCAPI.dao;

import java.util.Optional;

import com.CasinoCtC.CCtCAPI.model.RefreshToken;

public interface RefreshTokenDAO {

    void save(
            Integer userId,
            String tokenHash,
            java.util.Date expiryDate
    );

    Optional<RefreshToken> findValidToken(
            String tokenHash
    );

    void revokeToken(String tokenHash);
}