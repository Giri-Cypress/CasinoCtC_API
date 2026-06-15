package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.mapper.UserRowMapper;
import com.CasinoCtC.CCtCAPI.model.User;

@Repository
public class UserDAOImpl implements UserDAO {

    private static final Logger log = LoggerFactory.getLogger(UserDAOImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserDAOImpl(
            JdbcTemplate jdbcTemplate,
            UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public List<User> findAll() {
        try {
            String sql = "SELECT * FROM GSI.USERS ORDER BY UserID";
            return jdbcTemplate.query(sql, userRowMapper);
        } catch (Exception ex) {
            log.error("Error querying all users", ex);
            throw ex;
        }
    }

    @Override
    public Optional<User> findById(Integer id) {
        try {
            String sql = "SELECT * FROM GSI.USERS WHERE UserID = ?";
            List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
            return users.stream().findFirst();
        } catch (Exception ex) {
            log.error("Error querying user by id={}", id, ex);
            throw ex;
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            String sql = "SELECT * FROM GSI.USERS WHERE UserName = ?";
            List<User> users = jdbcTemplate.query(sql, userRowMapper, username);
            return users.stream().findFirst();
        } catch (Exception ex) {
            log.error("Error querying user by username={}", username, ex);
            throw ex;
        }
    }

    @Override
    public User save(User user) {
        try {
            String checkSql = "SELECT COUNT(*) FROM GSI.USERS WHERE UserID = ?";
            Integer count = jdbcTemplate.queryForObject(
                checkSql,
                Integer.class,
                user.getUserId()
            );

            if (count != null && count > 0) {
                String updateSql =
                    "UPDATE GSI.USERS SET " +
                    "UserName = ?, " +
                    "Password = ?, " +
                    "LocationID = ?, " +
                    "Status = ? " +
                    "WHERE UserID = ?";

                jdbcTemplate.update(
                    updateSql,
                    user.getUserName(),
                    user.getPassword(),
                    user.getLocationId(),
                    user.getStatus(),
                    user.getUserId()
                );
            } else {
                String insertSql =
                    "INSERT INTO GSI.USERS " +
                    "(UserID, UserName, Password, LocationID, Status) " +
                    "VALUES (?, ?, ?, ?, ?)";

                jdbcTemplate.update(
                    insertSql,
                    user.getUserId(),
                    user.getUserName(),
                    user.getPassword(),
                    user.getLocationId(),
                    user.getStatus()
                );
            }

            return findById(user.getUserId()).orElse(user);

        } catch (Exception ex) {
            log.error("Error saving user with ID {}", user.getUserId(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteById(Integer id) {
        try {
            String deleteUserSql = "DELETE FROM GSI.USERS WHERE UserID = ?";
            jdbcTemplate.update(deleteUserSql, id);
        } catch (Exception ex) {
            log.error("Error deleting user id={}", id, ex);
            throw ex;
        }
    }
}
