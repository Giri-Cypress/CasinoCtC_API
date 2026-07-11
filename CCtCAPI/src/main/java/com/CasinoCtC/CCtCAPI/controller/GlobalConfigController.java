package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CasinoCtC.CCtCAPI.dto.GlobalConfigResponse;
import com.CasinoCtC.CCtCAPI.dto.GlobalConfigUpdateRequest;
import com.CasinoCtC.CCtCAPI.service.GlobalConfigService;

@RestController
@RequestMapping("/api/admin/global-config")
@PreAuthorize("hasRole('ADMIN')")
public class GlobalConfigController {

    private final GlobalConfigService globalConfigService;

    public GlobalConfigController(GlobalConfigService globalConfigService) {
        this.globalConfigService = globalConfigService;
    }

    @GetMapping
    public ResponseEntity<List<GlobalConfigResponse>> getGlobalConfig() {
        return ResponseEntity.ok(globalConfigService.getGlobalConfig());
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateGlobalConfig(
            @RequestBody List<GlobalConfigUpdateRequest> request) {
        globalConfigService.updateGlobalConfig(request);
        return ResponseEntity.ok(Map.of("message", "Global configuration updated successfully."));
    }
}
