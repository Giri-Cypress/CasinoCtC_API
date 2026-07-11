package com.CasinoCtC.CCtCAPI.dto;

import java.util.ArrayList;
import java.util.List;

public class OverrideValidateRequest {

    private List<Integer> ruleNumbers = new ArrayList<>();
    private String userName;
    private String password;
    private Integer cashDifference;
    private Integer ticketDifference;

    public List<Integer> getRuleNumbers() { return ruleNumbers; }
    public void setRuleNumbers(List<Integer> ruleNumbers) { this.ruleNumbers = ruleNumbers; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getCashDifference() { return cashDifference; }
    public void setCashDifference(Integer cashDifference) { this.cashDifference = cashDifference; }

    public Integer getTicketDifference() { return ticketDifference; }
    public void setTicketDifference(Integer ticketDifference) { this.ticketDifference = ticketDifference; }
}
