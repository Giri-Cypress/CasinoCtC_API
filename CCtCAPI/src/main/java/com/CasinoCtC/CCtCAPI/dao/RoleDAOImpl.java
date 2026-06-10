package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository

public class RoleDAOImpl implements RoleDAO {

    private final JdbcTemplate jdbcTemplate;
   
    
    public RoleDAOImpl (
            JdbcTemplate jdbcTemplate) {
    	    this.jdbcTemplate = jdbcTemplate;
			}
    @Override
    public List<String> findRolesByUserId(Integer userId) {

        String sql = "SELECT r.rolename FROM GSI.roles r JOIN GSI.user_roles ur ON r.roleid = ur.userid WHERE ur.userid = ?";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("rolename"),
                userId
        );
    }
}