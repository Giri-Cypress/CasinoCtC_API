package com.CasinoCtC.CCtCAPI.dao;

import java.util.Optional;

import com.CasinoCtC.CCtCAPI.model.User;

public interface UserDAO {
    Optional<User> findByUsername(String username);
}

