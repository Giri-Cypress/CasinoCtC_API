package com.CasinoCtC.CCtCAPI.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.CasinoCtC.CCtCAPI.model.Role;

@Component
public class RoleRowMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {

        Role role = new Role();

        role.setId(rs.getLong("id"));
        role.setName(rs.getString("name"));

        return role;
    }
}
