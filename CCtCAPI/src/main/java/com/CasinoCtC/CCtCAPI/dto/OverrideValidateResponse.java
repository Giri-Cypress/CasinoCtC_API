package com.CasinoCtC.CCtCAPI.dto;

import java.util.ArrayList;
import java.util.List;

public class OverrideValidateResponse {

    private boolean valid;
    private String overrideUserName;
    private Integer overrideUserNumber;
    private List<Integer> approvedRuleNumbers = new ArrayList<>();
    private List<Integer> approvedRoleNumbers = new ArrayList<>();
    private List<Long> supervisorActivityNumbers = new ArrayList<>();

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getOverrideUserName() { return overrideUserName; }
    public void setOverrideUserName(String overrideUserName) { this.overrideUserName = overrideUserName; }

    public Integer getOverrideUserNumber() { return overrideUserNumber; }
    public void setOverrideUserNumber(Integer overrideUserNumber) { this.overrideUserNumber = overrideUserNumber; }

    public List<Integer> getApprovedRuleNumbers() { return approvedRuleNumbers; }
    public void setApprovedRuleNumbers(List<Integer> approvedRuleNumbers) { this.approvedRuleNumbers = approvedRuleNumbers; }

    public List<Integer> getApprovedRoleNumbers() { return approvedRoleNumbers; }
    public void setApprovedRoleNumbers(List<Integer> approvedRoleNumbers) { this.approvedRoleNumbers = approvedRoleNumbers; }

    public List<Long> getSupervisorActivityNumbers() { return supervisorActivityNumbers; }
    public void setSupervisorActivityNumbers(List<Long> supervisorActivityNumbers) { this.supervisorActivityNumbers = supervisorActivityNumbers; }
}
