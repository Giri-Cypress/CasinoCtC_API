package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class InventoryId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "location_number")
    private Integer locationNumber;

    @Column(name = "user_number")
    private Integer userNumber;

    public InventoryId() {
    }

    public InventoryId(Integer locationNumber, Integer userNumber) {
        this.locationNumber = locationNumber;
        this.userNumber = userNumber;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryId)) {
            return false;
        }
        InventoryId that = (InventoryId) o;
        return Objects.equals(locationNumber, that.locationNumber)
                && Objects.equals(userNumber, that.userNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationNumber, userNumber);
    }
}
