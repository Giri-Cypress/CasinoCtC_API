package com.CasinoCtC.CCtCAPI.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.RefreshTokenEntity;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByRefreshTokenHashAndRevokedFalseAndExpiryAtAfter(
            String refreshTokenHash,
            LocalDateTime now
    );
}
