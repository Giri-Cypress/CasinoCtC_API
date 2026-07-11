package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.entity.LocationEntity;
import com.CasinoCtC.CCtCAPI.model.Location;
import com.CasinoCtC.CCtCAPI.repository.LocationRepository;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAllByOrderByLocationNumberAsc().stream().map(this::toModel).collect(Collectors.toList());
    }

    public Location getLocationByNumber(Integer locationNumber) {
        return locationRepository.findById(locationNumber).map(this::toModel).orElse(null);
    }

    public Location saveLocation(Location location) {
        return toModel(locationRepository.save(toEntity(location)));
    }

    public void deleteLocation(Integer locationNumber) {
        locationRepository.deleteById(locationNumber);
    }

    private Location toModel(LocationEntity entity) {
        Location model = new Location();
        model.setLocationNumber(entity.getLocationNumber());
        model.setLocationName(entity.getLocationName());
        model.setAddress1(entity.getAddress1());
        model.setAddress2(entity.getAddress2());
        model.setCity(entity.getCity());
        model.setState(entity.getState());
        model.setZip(entity.getZip());
        model.setStatus(entity.getStatus());
        return model;
    }

    private LocationEntity toEntity(Location model) {
        LocationEntity entity = new LocationEntity();
        entity.setLocationNumber(model.getLocationNumber());
        entity.setLocationName(model.getLocationName());
        entity.setAddress1(model.getAddress1());
        entity.setAddress2(model.getAddress2());
        entity.setCity(model.getCity());
        entity.setState(model.getState());
        entity.setZip(model.getZip());
        entity.setStatus(model.getStatus() == null ? 1 : model.getStatus());
        return entity;
    }
}
