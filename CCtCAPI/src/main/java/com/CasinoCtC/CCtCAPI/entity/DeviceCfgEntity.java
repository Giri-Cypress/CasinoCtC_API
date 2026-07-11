package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(DeviceCfgId.class)
@Table(name = "devicecfg", schema = "gsi")
public class DeviceCfgEntity {

    @Id
    @Column(name = "station_name", nullable = false, length = 512)
    private String stationName;

    @Id
    @Column(name = "kinds", nullable = false)
    private Integer kinds;

    @Column(name = "location_number", nullable = false)
    private Integer locationNumber;

    @Column(name = "comport", nullable = false, length = 25)
    private String comport;

    @Column(name = "module", nullable = false, length = 25)
    private String module;

    @Column(name = "useflg", nullable = false)
    private Integer useflg;

    @Column(name = "ip", length = 15)
    private String ip;

    @Column(name = "port")
    private Integer port;

    @Column(name = "device_name", length = 512)
    private String deviceName;

    @Column(name = "ticket_ftp_id", length = 50)
    private String ticketFtpId;

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public Integer getKinds() { return kinds; }
    public void setKinds(Integer kinds) { this.kinds = kinds; }
    public Integer getLocationNumber() { return locationNumber; }
    public void setLocationNumber(Integer locationNumber) { this.locationNumber = locationNumber; }
    public String getComport() { return comport; }
    public void setComport(String comport) { this.comport = comport; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public Integer getUseflg() { return useflg; }
    public void setUseflg(Integer useflg) { this.useflg = useflg; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getTicketFtpId() { return ticketFtpId; }
    public void setTicketFtpId(String ticketFtpId) { this.ticketFtpId = ticketFtpId; }
}
