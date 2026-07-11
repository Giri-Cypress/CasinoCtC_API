package com.CasinoCtC.CCtCAPI.dto;

public class OverrideRuleResponse {

    private Integer ruleNumber;
    private String description;
    private Integer overrideRoleNumber;
    private String overrideRoleName;

    public Integer getRuleNumber() {
        return ruleNumber;
    }

    public void setRuleNumber(Integer ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOverrideRoleNumber() {
        return overrideRoleNumber;
    }

    public void setOverrideRoleNumber(Integer overrideRoleNumber) {
        this.overrideRoleNumber = overrideRoleNumber;
    }

    public String getOverrideRoleName() {
        return overrideRoleName;
    }

    public void setOverrideRoleName(String overrideRoleName) {
        this.overrideRoleName = overrideRoleName;
    }
}
