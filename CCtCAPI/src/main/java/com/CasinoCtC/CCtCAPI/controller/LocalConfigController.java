package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CasinoCtC.CCtCAPI.dto.LocalConfigLocationContext;
import com.CasinoCtC.CCtCAPI.dto.LocalConfigResponse;
import com.CasinoCtC.CCtCAPI.dto.LocalConfigUpdateRequest;
import com.CasinoCtC.CCtCAPI.service.LocalConfigService;

@RestController
@RequestMapping("/api/admin/local-config")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','CENTRAL_MANAGER')")
public class LocalConfigController {

    private final LocalConfigService localConfigService;

    public LocalConfigController(LocalConfigService localConfigService) {
        this.localConfigService = localConfigService;
    }

    @GetMapping("/locations")
    public ResponseEntity<LocalConfigLocationContext> getLocationContext(Authentication authentication) {
        boolean canManageAllLocations = canManageAllLocations(authentication);
        return ResponseEntity.ok(localConfigService.getLocationContext(authentication.getName(), canManageAllLocations));
    }

    @GetMapping
    public ResponseEntity<List<LocalConfigResponse>> getLocalConfig(
            Authentication authentication,
            @RequestParam(value = "locationNumber", required = false) Integer locationNumber) {
        boolean canManageAllLocations = canManageAllLocations(authentication);
        return ResponseEntity.ok(localConfigService.getLocalConfig(authentication.getName(), locationNumber, canManageAllLocations));
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateLocalConfig(
            Authentication authentication,
            @RequestParam(value = "locationNumber", required = false) Integer locationNumber,
            @RequestBody List<LocalConfigUpdateRequest> request) {
        boolean canManageAllLocations = canManageAllLocations(authentication);
        localConfigService.updateLocalConfig(authentication.getName(), locationNumber, canManageAllLocations, request);
        return ResponseEntity.ok(Map.of("message", "Local configuration updated successfully."));
    }

    private boolean canManageAllLocations(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream().anyMatch(authority -> {
            String role = authority.getAuthority();
            return "ROLE_ADMIN".equals(role)
                || "ROLE_MANAGER".equals(role)
                || "ROLE_CENTRAL_MANAGER".equals(role);
        });
    }
}
