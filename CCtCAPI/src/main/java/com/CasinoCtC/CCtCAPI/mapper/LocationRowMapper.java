package com.CasinoCtC.CCtCAPI.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.CasinoCtC.CCtCAPI.model.Location;

@Component
public class LocationRowMapper implements RowMapper<Location> {

    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {

        Location location = new Location();

        location.setLocationId(rs.getInt("LocationID"));
        location.setLocationName(rs.getString("Location_Name"));
        location.setStatus(rs.getInt("Status"));

        return location;
    }
}