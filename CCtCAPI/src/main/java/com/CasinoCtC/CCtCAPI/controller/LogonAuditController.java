package com.CasinoCtC.CCtCAPI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.CasinoCtC.CCtCAPI.dto.*;
import com.CasinoCtC.CCtCAPI.service.LogonAuditService;

@RestController
@RequestMapping("/api/logon-audit")
public class LogonAuditController {
    private final LogonAuditService service;
    public LogonAuditController(LogonAuditService service) { this.service = service; }
    @PostMapping("/login") public DeviceSelectionContextResponse startLogon(@RequestBody DeviceSelectionStartRequest request) { return service.startLogon(request); }
    @GetMapping("/device-selection-context") public DeviceSelectionContextResponse getContext(@RequestParam Integer locationNumber, @RequestParam Integer userNumber) { return service.getContext(locationNumber, userNumber); }
    @PutMapping("/selected-device") public ResponseEntity<Void> updateSelectedDevice(@RequestBody DeviceSelectionUpdateRequest request) { service.updateSelectedDevice(request); return ResponseEntity.noContent().build(); }
    @PostMapping("/logoff") public ResponseEntity<Void> logoff(@RequestBody DeviceSelectionUpdateRequest request) { service.logoff(request); return ResponseEntity.noContent().build(); }
}
