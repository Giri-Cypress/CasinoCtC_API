package com.CasinoCtC.CCtCAPI.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.dao.LocationDAO;
import com.CasinoCtC.CCtCAPI.dao.LocationMiscDAO;
import com.CasinoCtC.CCtCAPI.model.Location;
import com.CasinoCtC.CCtCAPI.model.LocationMisc;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationDAO locationDAO;

    @Autowired
    private LocationMiscDAO locationMiscDAO;

    @Override
    public List<Location> getAllLocations() {
        return locationDAO.findAll();
    }

    @Override
    public Location getLocationById(int id) {
        return locationDAO.findById(id).orElse(null);
    }

    @Override
    public Location saveLocation(Location location) {
        return locationDAO.save(location);
    }

    @Override
    public void deleteLocation(int id) {
        locationDAO.deleteById(id);
        locationMiscDAO.deleteByLocationId(id);
    }

    @Override
    public List<LocationMisc> getLocationMisc(int locationId) {
        return locationMiscDAO.findByLocationId(locationId);
    }

    @Override
    public void saveLocationMisc(int locationId, List<LocationMisc> list) {
        locationMiscDAO.deleteByLocationId(locationId);

        for (LocationMisc row : list) {
            row.setLocationId(locationId);
            if (row.getMiscValue() == null) {
                row.setMiscValue("");
            }
        }

        locationMiscDAO.insertAll(list);
    }
}