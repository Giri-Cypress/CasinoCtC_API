package com.CasinoCtC.CCtCAPI.dto;
import java.util.List;
public class DeviceSelectionContextResponse {
    private Long logonAuditNumber; private Integer locationNumber; private Integer userNumber; private String lastDeviceName; private List<DeviceSelectionDeviceResponse> devices;
    public Long getLogonAuditNumber() { return logonAuditNumber; } public void setLogonAuditNumber(Long logonAuditNumber) { this.logonAuditNumber = logonAuditNumber; }
    public Integer getLocationNumber() { return locationNumber; } public void setLocationNumber(Integer locationNumber) { this.locationNumber = locationNumber; }
    public Integer getUserNumber() { return userNumber; } public void setUserNumber(Integer userNumber) { this.userNumber = userNumber; }
    public String getLastDeviceName() { return lastDeviceName; } public void setLastDeviceName(String lastDeviceName) { this.lastDeviceName = lastDeviceName; }
    public List<DeviceSelectionDeviceResponse> getDevices() { return devices; } public void setDevices(List<DeviceSelectionDeviceResponse> devices) { this.devices = devices; }
}
