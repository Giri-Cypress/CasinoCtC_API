package com.CasinoCtC.CCtCAPI.service;

import java.util.List;

import com.CasinoCtC.CCtCAPI.model.Location;
import com.CasinoCtC.CCtCAPI.model.LocationMisc;

public interface LocationService {

    List<Location> getAllLocations();

    Location getLocationById(int id);

    Location saveLocation(Location location);

    void deleteLocation(int id);
    

    List<LocationMisc> getLocationMisc(int locationId);
    void saveLocationMisc(int locationId, List<LocationMisc> list);

}