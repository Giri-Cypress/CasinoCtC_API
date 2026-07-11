package com.CasinoCtC.CCtCAPI.dto;

import java.math.BigDecimal;

public class DailySummaryReportRow {

    private Integer locationNumber;
    private String businessDate;
    private Integer userNumber;
    private Long transactionNumber;
    private String status;
    private BigDecimal currencyTotal;
    private BigDecimal ticketCount;
    private BigDecimal grandTotal;

    public Integer getLocationNumber() {
        return locationNumber;
    }

    public void setLocationNumber(Integer locationNumber) {
        this.locationNumber = locationNumber;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }

    public Long getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(Long transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getCurrencyTotal() {
        return currencyTotal;
    }

    public void setCurrencyTotal(BigDecimal currencyTotal) {
        this.currencyTotal = currencyTotal;
    }

    public BigDecimal getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(BigDecimal ticketCount) {
        this.ticketCount = ticketCount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }
}
