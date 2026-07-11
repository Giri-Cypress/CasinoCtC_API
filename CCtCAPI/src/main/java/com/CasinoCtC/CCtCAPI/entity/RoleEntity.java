package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles", schema = "gsi")
public class RoleEntity {

    @Id
    @Column(name = "role_number")
    private Integer roleNumber;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "override_cash_limit")
    private Integer overrideCashLimit;

    @Column(name = "override_ticket_limit")
    private Integer overrideTicketLimit;

    public Integer getRoleNumber() {
        return roleNumber;
    }

    public void setRoleNumber(Integer roleNumber) {
        this.roleNumber = roleNumber;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOverrideCashLimit() {
        return overrideCashLimit;
    }

    public void setOverrideCashLimit(Integer overrideCashLimit) {
        this.overrideCashLimit = overrideCashLimit;
    }

    public Integer getOverrideTicketLimit() {
        return overrideTicketLimit;
    }

    public void setOverrideTicketLimit(Integer overrideTicketLimit) {
        this.overrideTicketLimit = overrideTicketLimit;
    }
}
