package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.model.Role;

@Repository
public class RoleDAOImpl implements RoleDAO {

    private static final Logger log = LoggerFactory.getLogger(RoleDAOImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public RoleDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> findRolesByUserId(Integer userId) {
        try {
            String sql =
                "SELECT r.RoleName " +
                "FROM GSI.ROLES r " +
                "JOIN GSI.USER_ROLES ur ON r.RoleID = ur.RoleID " +
                "WHERE ur.UserID = ?";

            return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("RoleName"),
                userId
            );
        } catch (Exception ex) {
            log.error("Error finding role names for userId={}", userId, ex);
            throw ex;
        }
    }

    @Override
    public List<Role> findAllRoles() {
        try {
            String sql =
                "SELECT RoleID, RoleName " +
                "FROM GSI.ROLES " +
                "ORDER BY RoleName";

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Role role = new Role();
                role.setId(rs.getLong("RoleID"));
                role.setName(rs.getString("RoleName"));
                return role;
            });
        } catch (Exception ex) {
            log.error("Error querying all roles", ex);
            throw ex;
        }
    }

    @Override
    public List<Integer> findRoleIdsByUserId(Integer userId) {
        try {
            String sql =
                "SELECT RoleID " +
                "FROM GSI.USER_ROLES " +
                "WHERE UserID = ? " +
                "ORDER BY RoleID";

            return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getInt("RoleID"),
                userId
            );
        } catch (Exception ex) {
            log.error("Error querying role IDs for userId={}", userId, ex);
            throw ex;
        }
    }

    @Override
    public void saveUserRoles(Integer userId, List<Integer> roleIds) {
        try {
            deleteUserRoles(userId);

            if (roleIds == null || roleIds.isEmpty()) {
                return;
            }

            String sql =
                "INSERT INTO GSI.USER_ROLES (UserID, RoleID) VALUES (?, ?)";

            for (Integer roleId : roleIds) {
                jdbcTemplate.update(sql, userId, roleId);
            }
        } catch (Exception ex) {
            log.error("Error saving user roles for userId={} with roleIds={}", userId, roleIds, ex);
            throw ex;
        }
    }

    @Override
    public void deleteUserRoles(Integer userId) {
        try {
            String sql = "DELETE FROM GSI.USER_ROLES WHERE UserID = ?";
            jdbcTemplate.update(sql, userId);
        } catch (Exception ex) {
            log.error("Error deleting user roles for userId={}", userId, ex);
            throw ex;
        }
    }
}