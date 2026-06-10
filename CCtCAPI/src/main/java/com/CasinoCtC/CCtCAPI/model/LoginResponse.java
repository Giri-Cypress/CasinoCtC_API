package com.CasinoCtC.CCtCAPI.model;

import java.util.List;

public class LoginResponse {

    private String username;
    private List<String> roles;
    private List<Menu> menus;
    private String locationName;
    private String token;
    private String refreshToken;

    public LoginResponse() {
    }

    public LoginResponse(
            String username,
            List<String> roles,
            List<Menu> menus) {

        this.username = username;
        this.roles = roles;
        this.menus = menus;
    }

    // ✅ Username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // ✅ Roles
    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    // ✅ Menus
    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    // ✅ Location
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    // ✅ JWT Token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    // ✅ JWT Refresh Token
	public String getRefreshToken() {
	    return refreshToken;
	}
	
	public void setRefreshToken(String refreshToken) {
	    this.refreshToken = refreshToken;
	}

}