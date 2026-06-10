package com.CasinoCtC.CCtCAPI.mapper; 

import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.CasinoCtC.CCtCAPI.model.User;

@Component
public class UserRowMapper implements RowMapper<User> {


    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {

        User user = new User();

        user.setUserId(rs.getInt("UserID"));
        user.setUserName(rs.getString("UserName"));
        user.setPassword(rs.getString("Password"));
        user.setLocationId(rs.getInt("LocationID"));
        user.setStatus(rs.getInt("Status"));   
        return user;
    }

}