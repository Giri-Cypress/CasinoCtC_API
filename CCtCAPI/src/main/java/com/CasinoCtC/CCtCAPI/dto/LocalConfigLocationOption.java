package com.CasinoCtC.CCtCAPI.dto;

public class LocalConfigLocationOption {
    private Integer locationNumber;
    private String locationName;

    public LocalConfigLocationOption() {}

    public LocalConfigLocationOption(Integer locationNumber, String locationName) {
        this.locationNumber = locationNumber;
        this.locationName = locationName;
    }

    public Integer getLocationNumber() { return locationNumber; }
    public void setLocationNumber(Integer locationNumber) { this.locationNumber = locationNumber; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
}
