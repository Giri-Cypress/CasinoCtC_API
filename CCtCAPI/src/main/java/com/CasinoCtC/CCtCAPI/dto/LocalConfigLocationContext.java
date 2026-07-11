package com.CasinoCtC.CCtCAPI.dto;

import java.util.ArrayList;
import java.util.List;

public class LocalConfigLocationContext {
    private Integer currentLocationNumber;
    private boolean canManageAllLocations;
    private List<LocalConfigLocationOption> locations = new ArrayList<>();

    public Integer getCurrentLocationNumber() { return currentLocationNumber; }
    public void setCurrentLocationNumber(Integer currentLocationNumber) { this.currentLocationNumber = currentLocationNumber; }
    public boolean isCanManageAllLocations() { return canManageAllLocations; }
    public void setCanManageAllLocations(boolean canManageAllLocations) { this.canManageAllLocations = canManageAllLocations; }
    public List<LocalConfigLocationOption> getLocations() { return locations; }
    public void setLocations(List<LocalConfigLocationOption> locations) { this.locations = locations; }
}
