package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import java.util.Objects;

public class DeviceCfgId implements Serializable {
    private String stationName;
    private Integer kinds;

    public DeviceCfgId() {}

    public DeviceCfgId(String stationName, Integer kinds) {
        this.stationName = stationName;
        this.kinds = kinds;
    }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public Integer getKinds() { return kinds; }
    public void setKinds(Integer kinds) { this.kinds = kinds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceCfgId)) return false;
        DeviceCfgId that = (DeviceCfgId) o;
        return Objects.equals(stationName, that.stationName) && Objects.equals(kinds, that.kinds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationName, kinds);
    }
}
