package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import com.CasinoCtC.CCtCAPI.mapper.UserRowMapper;
import com.CasinoCtC.CCtCAPI.model.User;

@Repository
public class UserDAOImpl implements UserDAO {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    // ✅ Constructor Injection
    public UserDAOImpl(
            JdbcTemplate jdbcTemplate,
            UserRowMapper userRowMapper) {

        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public Optional<User> findByUsername(String username) {

        String sql =
                "SELECT * FROM GSI.USERS WHERE UserName = ?";

        List<User> users =
                jdbcTemplate.query(
                        sql,
                        userRowMapper,
                        username
                );

        return users.stream().findFirst();
    }
}