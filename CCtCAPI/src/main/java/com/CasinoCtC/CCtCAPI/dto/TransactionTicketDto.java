package com.CasinoCtC.CCtCAPI.dto;

public class TransactionTicketDto {
    private String number;
    private Long amount; // cents
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
}
