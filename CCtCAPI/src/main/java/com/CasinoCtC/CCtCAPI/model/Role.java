package com.CasinoCtC.CCtCAPI.model;

public class Role {
    private Integer roleNumber;
    private String roleName;
    private Integer status;
    private Integer overrideCashLimit;
    private Integer overrideTicketLimit;

    public Integer getRoleNumber() { return roleNumber; }
    public void setRoleNumber(Integer roleNumber) { this.roleNumber = roleNumber; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getOverrideCashLimit() { return overrideCashLimit; }
    public void setOverrideCashLimit(Integer overrideCashLimit) { this.overrideCashLimit = overrideCashLimit; }

    public Integer getOverrideTicketLimit() { return overrideTicketLimit; }
    public void setOverrideTicketLimit(Integer overrideTicketLimit) { this.overrideTicketLimit = overrideTicketLimit; }
}
