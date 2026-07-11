package com.CasinoCtC.CCtCAPI.dto;

public class LocalConfigUpdateRequest {
    private Long lcNumber;
    private String lcGroup;
    private String lcLabel;
    private String lcValue;

    public Long getLcNumber() { return lcNumber; }
    public void setLcNumber(Long lcNumber) { this.lcNumber = lcNumber; }
    public String getLcGroup() { return lcGroup; }
    public void setLcGroup(String lcGroup) { this.lcGroup = lcGroup; }
    public String getLcLabel() { return lcLabel; }
    public void setLcLabel(String lcLabel) { this.lcLabel = lcLabel; }
    public String getLcValue() { return lcValue; }
    public void setLcValue(String lcValue) { this.lcValue = lcValue; }
}
