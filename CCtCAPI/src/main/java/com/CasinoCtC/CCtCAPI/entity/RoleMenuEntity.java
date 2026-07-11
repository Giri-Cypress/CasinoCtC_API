package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "role_menus", schema = "gsi")
public class RoleMenuEntity {

    @EmbeddedId
    private RoleMenuId id;

    @Column(name = "security_level", nullable = false)
    private Integer securityLevel;

    public RoleMenuId getId() {
        return id;
    }

    public void setId(RoleMenuId id) {
        this.id = id;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public Integer getRoleNumber() {
        return id != null ? id.getRoleNumber() : null;
    }

    public Integer getMenuNumber() {
        return id != null ? id.getMenuNumber() : null;
    }
}
