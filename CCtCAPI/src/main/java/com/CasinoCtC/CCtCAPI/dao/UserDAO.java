package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;
import java.util.Optional;

import com.CasinoCtC.CCtCAPI.model.User;

public interface UserDAO {
    List<User> findAll();
    Optional<User> findById(Integer id);
    Optional<User> findByUsername(String username);
    User save(User user);
    void deleteById(Integer id);
}