package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LocationMiscId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "location_number")
    private Integer locationNumber;

    @Column(name = "misc_number")
    private Integer miscNumber;

    public LocationMiscId() {
    }

    public LocationMiscId(Integer locationNumber, Integer miscNumber) {
        this.locationNumber = locationNumber;
        this.miscNumber = miscNumber;
    }

    public Integer getLocationNumber() {
        return locationNumber;
    }

    public void setLocationNumber(Integer locationNumber) {
        this.locationNumber = locationNumber;
    }

    public Integer getMiscNumber() {
        return miscNumber;
    }

    public void setMiscNumber(Integer miscNumber) {
        this.miscNumber = miscNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationMiscId)) return false;
        LocationMiscId that = (LocationMiscId) o;
        return Objects.equals(locationNumber, that.locationNumber) && Objects.equals(miscNumber, that.miscNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationNumber, miscNumber);
    }
}
