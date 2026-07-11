package com.CasinoCtC.CCtCAPI.dto;

public class DeviceSelectionDeviceResponse {
    private Integer locationNumber; private String stationName; private Integer kinds; private String deviceName; private String module; private String ticketFtpId;
    public DeviceSelectionDeviceResponse() {}
    public DeviceSelectionDeviceResponse(Integer locationNumber, String stationName, Integer kinds, String deviceName, String module, String ticketFtpId) { this.locationNumber = locationNumber; this.stationName = stationName; this.kinds = kinds; this.deviceName = deviceName; this.module = module; this.ticketFtpId = ticketFtpId; }
    public Integer getLocationNumber() { return locationNumber; } public void setLocationNumber(Integer locationNumber) { this.locationNumber = locationNumber; }
    public String getStationName() { return stationName; } public void setStationName(String stationName) { this.stationName = stationName; }
    public Integer getKinds() { return kinds; } public void setKinds(Integer kinds) { this.kinds = kinds; }
    public String getDeviceName() { return deviceName; } public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getModule() { return module; } public void setModule(String module) { this.module = module; }
    public String getTicketFtpId() { return ticketFtpId; } public void setTicketFtpId(String ticketFtpId) { this.ticketFtpId = ticketFtpId; }
}
