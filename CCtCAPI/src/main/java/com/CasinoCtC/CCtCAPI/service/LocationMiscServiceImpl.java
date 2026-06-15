package com.CasinoCtC.CCtCAPI.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.dao.LocationMiscDAO;
import com.CasinoCtC.CCtCAPI.model.LocationMisc;

@Service
public class LocationMiscServiceImpl implements LocationMiscService {

    @Autowired
    private LocationMiscDAO dao;

    // ✅ GET
    @Override
    public List<LocationMisc> getByLocationId(int locationId) {
        return dao.findByLocationId(locationId);
    }

    // ✅ SAVE (DELETE + INSERT PATTERN)
    @Override
    public void saveLocationMisc(int locationId, List<LocationMisc> list) {

        // ✅ 1. delete old
        dao.deleteByLocationId(locationId);

        // ✅ 2. set locationId for all rows
        int count = 1;
        for (LocationMisc m : list) {
            m.setLocationId(locationId);
            m.setMiscNo(count++);   // ensure sequence
        }

        // ✅ 3. insert new
        if (!list.isEmpty()) {
            dao.insertAll(list);
        }
    }
}

