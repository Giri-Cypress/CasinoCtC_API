package com.CasinoCtC.CCtCAPI.dto;

import java.time.LocalDate;

public class HeaderCardReportRequest {
    private LocalDate businessDate;

    public LocalDate getBusinessDate() { return businessDate; }
    public void setBusinessDate(LocalDate businessDate) { this.businessDate = businessDate; }
}
