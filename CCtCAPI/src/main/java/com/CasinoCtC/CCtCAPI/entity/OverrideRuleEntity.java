package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "override_rules", schema = "gsi")
public class OverrideRuleEntity {

    @Id
    @Column(name = "rule_number")
    private Integer ruleNumber;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Column(name = "override_role_number", nullable = true)
    private Integer overrideRoleNumber;

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
}
