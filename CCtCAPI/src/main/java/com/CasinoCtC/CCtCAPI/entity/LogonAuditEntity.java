package com.CasinoCtC.CCtCAPI.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "logon_audit", schema = "gsi")
public class LogonAuditEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "lh_number") private Long lhNumber;
    @Column(name = "location_number", nullable = false) private Integer locationNumber;
    @Column(name = "user_number", nullable = false) private Integer userNumber;
    @Column(name = "logon", nullable = false) private Integer logon;
    @Column(name = "device_name", length = 512) private String deviceName;
    @Column(name = "logon_datetime", nullable = false) private LocalDateTime logonDatetime;
    public Long getLhNumber() { return lhNumber; } public void setLhNumber(Long lhNumber) { this.lhNumber = lhNumber; }
    public Integer getLocationNumber() { return locationNumber; } public void setLocationNumber(Integer locationNumber) { this.locationNumber = locationNumber; }
    public Integer getUserNumber() { return userNumber; } public void setUserNumber(Integer userNumber) { this.userNumber = userNumber; }
    public Integer getLogon() { return logon; } public void setLogon(Integer logon) { this.logon = logon; }
    public String getDeviceName() { return deviceName; } public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public LocalDateTime getLogonDatetime() { return logonDatetime; } public void setLogonDatetime(LocalDateTime logonDatetime) { this.logonDatetime = logonDatetime; }
}
