package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "location_misc", schema = "gsi")
public class LocationMiscEntity {

    @EmbeddedId
    private LocationMiscId id;

    @Column(name = "misc_name", nullable = false, length = 100)
    private String miscName;

    @Column(name = "misc_value", length = 255)
    private String miscValue;

    public LocationMiscId getId() {
        return id;
    }

    public void setId(LocationMiscId id) {
        this.id = id;
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

    public Integer getLocationNumber() {
        return id != null ? id.getLocationNumber() : null;
    }

    public Integer getMiscNumber() {
        return id != null ? id.getMiscNumber() : null;
    }
}
