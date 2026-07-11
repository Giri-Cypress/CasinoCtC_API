package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RoleMenuId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "role_number")
    private Integer roleNumber;

    @Column(name = "menu_number")
    private Integer menuNumber;

    public RoleMenuId() {
    }

    public RoleMenuId(Integer roleNumber, Integer menuNumber) {
        this.roleNumber = roleNumber;
        this.menuNumber = menuNumber;
    }

    public Integer getRoleNumber() {
        return roleNumber;
    }

    public void setRoleNumber(Integer roleNumber) {
        this.roleNumber = roleNumber;
    }

    public Integer getMenuNumber() {
        return menuNumber;
    }

    public void setMenuNumber(Integer menuNumber) {
        this.menuNumber = menuNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleMenuId)) return false;
        RoleMenuId that = (RoleMenuId) o;
        return Objects.equals(roleNumber, that.roleNumber) && Objects.equals(menuNumber, that.menuNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleNumber, menuNumber);
    }
}
