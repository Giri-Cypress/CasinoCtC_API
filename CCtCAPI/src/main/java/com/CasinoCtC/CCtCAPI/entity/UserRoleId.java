package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserRoleId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "user_number")
    private Integer userNumber;

    @Column(name = "role_number")
    private Integer roleNumber;

    public UserRoleId() {
    }

    public UserRoleId(Integer userNumber, Integer roleNumber) {
        this.userNumber = userNumber;
        this.roleNumber = roleNumber;
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }

    public Integer getRoleNumber() {
        return roleNumber;
    }

    public void setRoleNumber(Integer roleNumber) {
        this.roleNumber = roleNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRoleId)) return false;
        UserRoleId that = (UserRoleId) o;
        return Objects.equals(userNumber, that.userNumber) && Objects.equals(roleNumber, that.roleNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userNumber, roleNumber);
    }
}
