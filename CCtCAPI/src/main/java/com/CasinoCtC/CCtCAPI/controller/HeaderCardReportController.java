package com.CasinoCtC.CCtCAPI.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardReportRow;
import com.CasinoCtC.CCtCAPI.service.HeaderCardReportService;

@RestController
@RequestMapping("/api/header-card-reports")
public class HeaderCardReportController {
    private final HeaderCardReportService service;
    public HeaderCardReportController(HeaderCardReportService service) { this.service = service; }

    @GetMapping("/unassigned")
    public List<HeaderCardReportRow> getUnassigned() { return service.getUnassignedHeaderCards(); }

    @GetMapping("/unprocessed")
    public List<HeaderCardReportRow> getUnprocessed(@RequestParam("businessDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate businessDate) {
        return service.getUnprocessedHeaderCards(businessDate);
    }

    @GetMapping("/processed")
    public List<HeaderCardReportRow> getProcessed(@RequestParam("businessDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate businessDate) {
        return service.getProcessedHeaderCards(businessDate);
    }
}
