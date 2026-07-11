package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class InventoryDtlId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "location_number")
    private Integer locationNumber;

    @Column(name = "user_number")
    private Integer userNumber;

    @Column(name = "denom_number")
    private Integer denomNumber;

    public InventoryDtlId() {
    }

    public InventoryDtlId(Integer locationNumber, Integer userNumber, Integer denomNumber) {
        this.locationNumber = locationNumber;
        this.userNumber = userNumber;
        this.denomNumber = denomNumber;
    }

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

    public Integer getDenomNumber() {
        return denomNumber;
    }

    public void setDenomNumber(Integer denomNumber) {
        this.denomNumber = denomNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryDtlId)) {
            return false;
        }
        InventoryDtlId that = (InventoryDtlId) o;
        return Objects.equals(locationNumber, that.locationNumber)
                && Objects.equals(userNumber, that.userNumber)
                && Objects.equals(denomNumber, that.denomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationNumber, userNumber, denomNumber);
    }
}
