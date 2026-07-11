package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users", schema = "gsi")
public class UserEntity {

    @Id
    @Column(name = "user_number")
    private Integer userNumber;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(name = "password_hash", nullable = false, length = 256)
    private String passwordHash;

    @Column(name = "location_number", nullable = false)
    private Integer locationNumber;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "last_login_at")
    private java.time.LocalDateTime lastLoginAt;

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Integer getLocationNumber() {
        return locationNumber;
    }

    public void setLocationNumber(Integer locationNumber) {
        this.locationNumber = locationNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public java.time.LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(java.time.LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
