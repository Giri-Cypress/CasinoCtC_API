package com.CasinoCtC.CCtCAPI.dto;

public class InventoryReportRow {

    private Integer locationNumber;
    private Integer userNumber;
    private String archiveDate;
    private Long cashAmount;
    private Integer denominationNumber;
    private String denominationDescription;
    private Long denominationAmount;

    public Integer getLocationNumber() {
        return locationNumber;
    }

    public void setLocationNumber(Integer locationNumber) {
        this.locationNumber = locationNumber;
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }

    public String getArchiveDate() { return archiveDate; }
    public void setArchiveDate(String archiveDate) { this.archiveDate = archiveDate; }

    public Long getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Long cashAmount) {
        this.cashAmount = cashAmount;
    }

    public Integer getDenominationNumber() {
        return denominationNumber;
    }

    public void setDenominationNumber(Integer denominationNumber) {
        this.denominationNumber = denominationNumber;
    }

    public String getDenominationDescription() {
        return denominationDescription;
    }

    public void setDenominationDescription(String denominationDescription) {
        this.denominationDescription = denominationDescription;
    }

    public Long getDenominationAmount() {
        return denominationAmount;
    }

    public void setDenominationAmount(Long denominationAmount) {
        this.denominationAmount = denominationAmount;
    }
}
