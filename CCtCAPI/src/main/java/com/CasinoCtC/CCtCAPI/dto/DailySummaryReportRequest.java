package com.CasinoCtC.CCtCAPI.dto;

import java.time.LocalDate;

public class DailySummaryReportRequest {

    private Integer locationNumberFrom;
    private Integer locationNumberTo;
    private LocalDate businessDateFrom;
    private LocalDate businessDateTo;
    private Integer userNumberFrom;
    private Integer userNumberTo;
    private String transactionStatus = "PROCESSED";

    public Integer getLocationNumberFrom() {
        return locationNumberFrom;
    }

    public void setLocationNumberFrom(Integer locationNumberFrom) {
        this.locationNumberFrom = locationNumberFrom;
    }

    public Integer getLocationNumberTo() {
        return locationNumberTo;
    }

    public void setLocationNumberTo(Integer locationNumberTo) {
        this.locationNumberTo = locationNumberTo;
    }

    public LocalDate getBusinessDateFrom() {
        return businessDateFrom;
    }

    public void setBusinessDateFrom(LocalDate businessDateFrom) {
        this.businessDateFrom = businessDateFrom;
    }

    public LocalDate getBusinessDateTo() {
        return businessDateTo;
    }

    public void setBusinessDateTo(LocalDate businessDateTo) {
        this.businessDateTo = businessDateTo;
    }

    public Integer getUserNumberFrom() {
        return userNumberFrom;
    }

    public void setUserNumberFrom(Integer userNumberFrom) {
        this.userNumberFrom = userNumberFrom;
    }

    public Integer getUserNumberTo() {
        return userNumberTo;
    }

    public void setUserNumberTo(Integer userNumberTo) {
        this.userNumberTo = userNumberTo;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
