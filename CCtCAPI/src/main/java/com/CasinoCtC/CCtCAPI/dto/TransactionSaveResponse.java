package com.CasinoCtC.CCtCAPI.dto;

public class TransactionSaveResponse {
    private Long transNumber;
    private String status;
    private Long totalCurrency; // cents
    private Integer ticketCount;
    private Long totalTicketAmount; // cents
    private Long totalAmount; // cents

    public Long getTransNumber() { return transNumber; }
    public void setTransNumber(Long transNumber) { this.transNumber = transNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getTotalCurrency() { return totalCurrency; }
    public void setTotalCurrency(Long totalCurrency) { this.totalCurrency = totalCurrency; }
    public Integer getTicketCount() { return ticketCount; }
    public void setTicketCount(Integer ticketCount) { this.ticketCount = ticketCount; }
    public Long getTotalTicketAmount() { return totalTicketAmount; }
    public void setTotalTicketAmount(Long totalTicketAmount) { this.totalTicketAmount = totalTicketAmount; }
    public Long getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Long totalAmount) { this.totalAmount = totalAmount; }
}
