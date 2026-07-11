package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CasinoCtC.CCtCAPI.dto.HeaderCardAssignRow;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardAssignUpdateRequest;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardReportRequest;
import com.CasinoCtC.CCtCAPI.service.HeaderCardAssignService;

@RestController
@RequestMapping("/api/header-card-assign")
public class HeaderCardAssignController {

    private final HeaderCardAssignService service;

    public HeaderCardAssignController(HeaderCardAssignService service) {
        this.service = service;
    }

    @GetMapping
    public List<HeaderCardAssignRow> getAll() {
        return service.getAll();
    }

    @GetMapping("/{headerCardId}")
    public HeaderCardAssignRow getByHeaderCardId(@PathVariable Long headerCardId) {
        return service.getByHeaderCardId(headerCardId);
    }

    @PutMapping("/{headerCardId}")
    public HeaderCardAssignRow update(@PathVariable Long headerCardId,
            @RequestBody HeaderCardAssignUpdateRequest request) {
        return service.update(headerCardId, request);
    }

    @DeleteMapping("/{headerCardId}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long headerCardId) {
        service.delete(headerCardId);
        return ResponseEntity.ok(Map.of("message", "Header Card assignment deleted successfully."));
    }

    @GetMapping("/reports/unassigned")
    public List<Map<String, Object>> getUnassignedHeaderCardReport() {
        return service.getUnassignedReport();
    }

    @PostMapping("/reports/unprocessed")
    public List<Map<String, Object>> getUnprocessedHeaderCardReport(
            @RequestBody HeaderCardReportRequest request) {
        return service.getUnprocessedReport(request == null ? null : request.getBusinessDate());
    }

    @PostMapping("/reports/processed")
    public List<Map<String, Object>> getProcessedHeaderCardReport(
            @RequestBody HeaderCardReportRequest request) {
        return service.getProcessedReport(request == null ? null : request.getBusinessDate());
    }
}
