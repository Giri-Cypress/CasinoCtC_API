package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CasinoCtC.CCtCAPI.entity.DeviceCfgEntity;
import com.CasinoCtC.CCtCAPI.service.DeviceCfgService;

@RestController
@RequestMapping("/api/admin/device-config")
@PreAuthorize("hasRole('ADMIN')")
public class DeviceCfgController {

    private final DeviceCfgService service;

    public DeviceCfgController(DeviceCfgService service) {
        this.service = service;
    }

    @GetMapping
    public List<DeviceCfgEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/location/{locationNumber}/active")
    public List<DeviceCfgEntity> getActiveByLocation(@PathVariable Integer locationNumber) {
        return service.getActiveByLocation(locationNumber);
    }

    @GetMapping("/{stationName}/{kinds}")
    public DeviceCfgEntity getById(@PathVariable String stationName, @PathVariable Integer kinds) {
        return service.getById(stationName, kinds);
    }

    @PostMapping
    public DeviceCfgEntity save(@RequestBody DeviceCfgEntity request) {
        return service.save(request);
    }

    @DeleteMapping("/{stationName}/{kinds}")
    public ResponseEntity<Void> delete(@PathVariable String stationName, @PathVariable Integer kinds) {
        service.delete(stationName, kinds);
        return ResponseEntity.noContent().build();
    }
}
