package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.CasinoCtC.CCtCAPI.model.Location;
import com.CasinoCtC.CCtCAPI.model.LocationMisc;
import com.CasinoCtC.CCtCAPI.service.LocationService;
import com.CasinoCtC.CCtCAPI.service.LocationMiscService;

@RestController
@RequestMapping("/api/locations")
@PreAuthorize("hasRole('ADMIN')")
public class LocationController {

    private final LocationService locationJpaService;
    private final LocationMiscService locationMiscJpaService;

    public LocationController(LocationService locationJpaService, LocationMiscService locationMiscJpaService) {
        this.locationJpaService = locationJpaService;
        this.locationMiscJpaService = locationMiscJpaService;
    }

    @GetMapping
    public List<Location> getAllLocations() {
        return locationJpaService.getAllLocations();
    }

    @GetMapping("/{locationNumber}")
    public Location getLocationByNumber(@PathVariable Integer locationNumber) {
        return locationJpaService.getLocationByNumber(locationNumber);
    }

    @PostMapping
    public Location saveLocation(@RequestBody Location location) {
        return locationJpaService.saveLocation(location);
    }

    @DeleteMapping("/{locationNumber}")
    public void deleteLocation(@PathVariable Integer locationNumber) {
        locationJpaService.deleteLocation(locationNumber);
    }

    @GetMapping("/{locationNumber}/misc")
    public List<LocationMisc> getLocationMisc(@PathVariable Integer locationNumber) {
        return locationMiscJpaService.getByLocationNumber(locationNumber);
    }

    @PostMapping("/{locationNumber}/misc")
    public ResponseEntity<Void> saveLocationMisc(@PathVariable Integer locationNumber, @RequestBody List<LocationMisc> list) {
        locationMiscJpaService.saveLocationMisc(locationNumber, list);
        return ResponseEntity.ok().build();
    }
}
