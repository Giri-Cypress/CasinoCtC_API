package com.CasinoCtC.CCtCAPI.model;

public class LocationMisc {

    private int locationId;
    private int miscNo;
    private String miscName;
    private String miscValue;

    // ✅ Getters & Setters

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getMiscNo() {
        return miscNo;
    }

    public void setMiscNo(int miscNo) {
        this.miscNo = miscNo;
    }

    public String getMiscName() {
        return miscName;
    }

    public void setMiscName(String miscName) {
        this.miscName = miscName;
    }

    public String getMiscValue() {
        return miscValue;
    }

    public void setMiscValue(String miscValue) {
        this.miscValue = miscValue;
    }
}
