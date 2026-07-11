package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import java.util.Objects;

public class InventoryArchiveDtlId implements Serializable {
    private Long invarchNumber;
    private Integer locationNumber;
    private Integer denomNumber;

    public InventoryArchiveDtlId() {}

    public InventoryArchiveDtlId(Long invarchNumber, Integer locationNumber, Integer denomNumber) {
        this.invarchNumber = invarchNumber;
        this.locationNumber = locationNumber;
        this.denomNumber = denomNumber;
    }

    public Long getInvarchNumber() { return invarchNumber; }
    public void setInvarchNumber(Long invarchNumber) { this.invarchNumber = invarchNumber; }

    public Integer getLocationNumber() { return locationNumber; }
    public void setLocationNumber(Integer locationNumber) { this.locationNumber = locationNumber; }

    public Integer getDenomNumber() { return denomNumber; }
    public void setDenomNumber(Integer denomNumber) { this.denomNumber = denomNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryArchiveDtlId)) return false;
        InventoryArchiveDtlId that = (InventoryArchiveDtlId) o;
        return Objects.equals(invarchNumber, that.invarchNumber) &&
               Objects.equals(locationNumber, that.locationNumber) &&
               Objects.equals(denomNumber, that.denomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invarchNumber, locationNumber, denomNumber);
    }
}
