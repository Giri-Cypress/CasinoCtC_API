package com.CasinoCtC.CCtCAPI.dto;

import java.util.ArrayList;
import java.util.List;

public class GlobalConfigResponse {
    private Long gcNumber;
    private String gcGroup;
    private String gcLabel;
    private String gcValue;
    private Integer gcStatus;
    private List<String> options = new ArrayList<>();

    public Long getGcNumber() {
        return gcNumber;
    }

    public void setGcNumber(Long gcNumber) {
        this.gcNumber = gcNumber;
    }

    public String getGcGroup() {
        return gcGroup;
    }

    public void setGcGroup(String gcGroup) {
        this.gcGroup = gcGroup;
    }

    public String getGcLabel() {
        return gcLabel;
    }

    public void setGcLabel(String gcLabel) {
        this.gcLabel = gcLabel;
    }

    public String getGcValue() {
        return gcValue;
    }

    public void setGcValue(String gcValue) {
        this.gcValue = gcValue;
    }

    public Integer getGcStatus() {
        return gcStatus;
    }

    public void setGcStatus(Integer gcStatus) {
        this.gcStatus = gcStatus;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options == null ? new ArrayList<>() : options;
    }
}
