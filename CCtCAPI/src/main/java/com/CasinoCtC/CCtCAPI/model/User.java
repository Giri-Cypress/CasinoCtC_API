package com.CasinoCtC.CCtCAPI.model;

public class User {
    private Integer userNumber;
    private String userName;
    private String passwordHash;
    private Integer locationNumber;
    private Integer status;
    private java.time.LocalDateTime lastLoginAt;

    public Integer getUserNumber() { return userNumber; }
    public void setUserNumber(Integer userNumber) { this.userNumber = userNumber; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Integer getLocationNumber() { return locationNumber; }
    public void setLocationNumber(Integer locationNumber) { this.locationNumber = locationNumber; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public java.time.LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(java.time.LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
