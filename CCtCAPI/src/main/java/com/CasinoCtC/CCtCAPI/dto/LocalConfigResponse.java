package com.CasinoCtC.CCtCAPI.dto;

import java.util.ArrayList;
import java.util.List;

public class LocalConfigResponse {
    private Long lcNumber;
    private Integer lcLocation;
    private String lcGroup;
    private String lcLabel;
    private String lcValue;
    private Integer lcStatus;
    private boolean defaultFromGlobal;
    private List<String> options = new ArrayList<>();

    public Long getLcNumber() { return lcNumber; }
    public void setLcNumber(Long lcNumber) { this.lcNumber = lcNumber; }
    public Integer getLcLocation() { return lcLocation; }
    public void setLcLocation(Integer lcLocation) { this.lcLocation = lcLocation; }
    public String getLcGroup() { return lcGroup; }
    public void setLcGroup(String lcGroup) { this.lcGroup = lcGroup; }
    public String getLcLabel() { return lcLabel; }
    public void setLcLabel(String lcLabel) { this.lcLabel = lcLabel; }
    public String getLcValue() { return lcValue; }
    public void setLcValue(String lcValue) { this.lcValue = lcValue; }
    public Integer getLcStatus() { return lcStatus; }
    public void setLcStatus(Integer lcStatus) { this.lcStatus = lcStatus; }
    public boolean isDefaultFromGlobal() { return defaultFromGlobal; }
    public void setDefaultFromGlobal(boolean defaultFromGlobal) { this.defaultFromGlobal = defaultFromGlobal; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}
