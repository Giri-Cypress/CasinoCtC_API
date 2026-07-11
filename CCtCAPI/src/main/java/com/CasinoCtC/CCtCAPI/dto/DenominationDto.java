package com.CasinoCtC.CCtCAPI.dto;

public class DenominationDto {
    private Integer denomNumber;
    private Long denomValue;
    private String description;
    private Integer active;
    private Integer displayOrder;

    public Integer getDenomNumber() { return denomNumber; }
    public void setDenomNumber(Integer denomNumber) { this.denomNumber = denomNumber; }
    public Long getDenomValue() { return denomValue; }
    public void setDenomValue(Long denomValue) { this.denomValue = denomValue; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getActive() { return active; }
    public void setActive(Integer active) { this.active = active; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
