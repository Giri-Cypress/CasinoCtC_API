package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CasinoCtC.CCtCAPI.dto.DailySummaryReportRequest;
import com.CasinoCtC.CCtCAPI.dto.DailySummaryReportRow;
import com.CasinoCtC.CCtCAPI.dto.InventoryReportRequest;
import com.CasinoCtC.CCtCAPI.dto.InventoryReportRow;
import com.CasinoCtC.CCtCAPI.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/daily-summary")
    public List<DailySummaryReportRow> getDailySummaryReport(
            @RequestBody DailySummaryReportRequest request,
            Authentication authentication) {

        return reportService.getDailySummaryReport(
                request,
                authentication.getName());
    }

    @PostMapping("/inventory")
    public List<InventoryReportRow> getInventoryReport(
            @RequestBody InventoryReportRequest request,
            Authentication authentication) {

        return reportService.getInventoryReport(
                request,
                authentication.getName());
    }

    @GetMapping("/inventory/clear-enabled/{locationNumber}")
    public Map<String, Object> isInventoryClearEnabled(
            @PathVariable Integer locationNumber) {

        boolean enabled =
                reportService.isInventoryClearEnabled(locationNumber);

        return Map.of(
                "enabled",
                enabled);
    }

}
