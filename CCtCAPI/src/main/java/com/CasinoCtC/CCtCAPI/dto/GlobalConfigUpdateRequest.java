package com.CasinoCtC.CCtCAPI.dto;

public class GlobalConfigUpdateRequest {
    private Long gcNumber;
    private String gcValue;

    public Long getGcNumber() {
        return gcNumber;
    }

    public void setGcNumber(Long gcNumber) {
        this.gcNumber = gcNumber;
    }

    public String getGcValue() {
        return gcValue;
    }

    public void setGcValue(String gcValue) {
        this.gcValue = gcValue;
    }
}
