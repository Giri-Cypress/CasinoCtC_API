package com.CasinoCtC.CCtCAPI.dto;

public class AuthUserContextResponse {
    private Integer userNumber;
    private String userName;
    private Integer locationNumber;
    private String locationName;

    public Integer getUserNumber() { return userNumber; }
    public void setUserNumber(Integer userNumber) { this.userNumber = userNumber; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Integer getLocationNumber() { return locationNumber; }
    public void setLocationNumber(Integer locationNumber) { this.locationNumber = locationNumber; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
}
