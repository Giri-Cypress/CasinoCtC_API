package com.CasinoCtC.CCtCAPI.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.CasinoCtC.CCtCAPI.mapper.LocationRowMapper;
import com.CasinoCtC.CCtCAPI.model.Location;

@Repository

public class LocationDAOImpl implements LocationDAO {

    private final JdbcTemplate jdbcTemplate;
    private final LocationRowMapper locationRowMapper;
    
    public LocationDAOImpl (
            JdbcTemplate jdbcTemplate,		
            LocationRowMapper locationRowMapper) {
    	    this.jdbcTemplate = jdbcTemplate;
    	    this.locationRowMapper = locationRowMapper;
    }
    

    @Override
    public Optional<Location> findById(Integer locationId) {

        String sql = "SELECT * FROM GSI.LOCATIONS WHERE LocationID = ?";

        List<Location> list = jdbcTemplate.query(
                sql,
                locationRowMapper,
                locationId
        );

        return list.stream().findFirst();
    }
}