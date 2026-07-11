package com.CasinoCtC.CCtCAPI.controller;

import com.CasinoCtC.CCtCAPI.dto.report.InventoryClearEnabledRequest;
import com.CasinoCtC.CCtCAPI.dto.report.InventoryClearRequest;
import com.CasinoCtC.CCtCAPI.dto.report.InventoryClearResponse;
import com.CasinoCtC.CCtCAPI.service.InventoryClearService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports/inventory")
public class InventoryClearController {
    private final InventoryClearService inventoryClearService;

    public InventoryClearController(InventoryClearService inventoryClearService) {
        this.inventoryClearService = inventoryClearService;
    }

    @PostMapping("/clear-enabled")
    public ResponseEntity<Map<String, Boolean>> isClearInventoryEnabled(
            @RequestBody InventoryClearEnabledRequest request) {
        return ResponseEntity.ok(Map.of(
                "enabled",
                inventoryClearService.isClearInventoryEnabled(request.getLocationNumber())
        ));
    }

    @PostMapping("/clear")
    public ResponseEntity<InventoryClearResponse> clearInventory(@RequestBody InventoryClearRequest request) {
        return ResponseEntity.ok(inventoryClearService.archiveAndClear(request));
    }
}
