package com.CasinoCtC.CCtCAPI.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUserName(String userName);

    @Modifying
    @Query("""
        UPDATE UserEntity u
           SET u.lastLoginAt = :lastLoginAt
         WHERE u.userNumber = :userNumber
    """)
    int updateLastLoginAt(
            @Param("userNumber") Integer userNumber,
            @Param("lastLoginAt") LocalDateTime lastLoginAt);
}