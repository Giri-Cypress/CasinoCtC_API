package com.CasinoCtC.CCtCAPI.model;

public class TransactionHeaderResponse {
    private Integer transNumber;
    private String status;
    private Boolean existing;

    public TransactionHeaderResponse() {}

    public TransactionHeaderResponse(Integer transNumber, String status, Boolean existing) {
        this.transNumber = transNumber;
        this.status = status;
        this.existing = existing;
    }

    public Integer getTransNumber() { return transNumber; }
    public void setTransNumber(Integer transNumber) { this.transNumber = transNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getExisting() { return existing; }
    public void setExisting(Boolean existing) { this.existing = existing; }
}
