package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.model.LocationMisc;

@Repository
public class LocationMiscDAOImpl implements LocationMiscDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ✅ GET MISC BY LOCATION
    @Override
    public List<LocationMisc> findByLocationId(int locationId) {

        String sql = "SELECT * FROM GSI.LOCATION_MISC WHERE LocationID = ?";

        return jdbcTemplate.query(sql, new Object[]{locationId}, (rs, rowNum) -> {
            LocationMisc m = new LocationMisc();

            m.setLocationId(rs.getInt("LocationID"));
            m.setMiscNo(rs.getInt("MiscNo"));
            m.setMiscName(rs.getString("MiscName"));
            m.setMiscValue(rs.getString("MiscValue"));

            return m;
        });
    }

    // ✅ INSERT ALL ROWS
    @Override
    public void insertAll(List<LocationMisc> list) {

        String sql = "INSERT INTO GSI.LOCATION_MISC (LocationID, MiscNo, MiscName, MiscValue) VALUES (?, ?, ?, ?)";

        for (LocationMisc m : list) {
            jdbcTemplate.update(sql,
                m.getLocationId(),
                m.getMiscNo(),
                m.getMiscName(),
                m.getMiscValue()
            );
        }
    }

    // ✅ DELETE ALL FOR LOCATION (IMPORTANT FOR UPDATE)
    @Override
    public void deleteByLocationId(int locationId) {

        String sql = "DELETE FROM GSI.LOCATION_MISC WHERE LocationID = ?";

        jdbcTemplate.update(sql, locationId);
    }
}
