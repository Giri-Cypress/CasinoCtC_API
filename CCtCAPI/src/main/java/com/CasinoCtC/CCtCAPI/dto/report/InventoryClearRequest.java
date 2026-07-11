package com.CasinoCtC.CCtCAPI.dto.report;

public class InventoryClearRequest {
    private Integer locationNumberFrom;
    private Integer locationNumberTo;
    private Integer userNumberFrom;
    private Integer userNumberTo;
    private Integer clearedByUserNumber;

    public Integer getLocationNumberFrom() { return locationNumberFrom; }
    public void setLocationNumberFrom(Integer locationNumberFrom) { this.locationNumberFrom = locationNumberFrom; }

    public Integer getLocationNumberTo() { return locationNumberTo; }
    public void setLocationNumberTo(Integer locationNumberTo) { this.locationNumberTo = locationNumberTo; }

    public Integer getUserNumberFrom() { return userNumberFrom; }
    public void setUserNumberFrom(Integer userNumberFrom) { this.userNumberFrom = userNumberFrom; }

    public Integer getUserNumberTo() { return userNumberTo; }
    public void setUserNumberTo(Integer userNumberTo) { this.userNumberTo = userNumberTo; }

    public Integer getClearedByUserNumber() { return clearedByUserNumber; }
    public void setClearedByUserNumber(Integer clearedByUserNumber) { this.clearedByUserNumber = clearedByUserNumber; }
}
