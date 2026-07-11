package com.CasinoCtC.CCtCAPI.dto;

import java.math.BigDecimal;

public class TransactionInquiryRowResponse {

    private Long transactionNumber;
    private String businessDate;
    private String collectionDate;
    private String boxNumber;
    private String headerCard;
    private Integer employeeNumber;
    private String status;
    private BigDecimal currencyTotal;
    private BigDecimal ticketTotal;
    private BigDecimal grandTotal;

    public Long getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(Long transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(String collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }

    public Integer getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(Integer employeeNumber) {
        this.employeeNumber = employeeNumber;
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

    public BigDecimal getTicketTotal() {
        return ticketTotal;
    }

    public void setTicketTotal(BigDecimal ticketTotal) {
        this.ticketTotal = ticketTotal;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }
    public String getHeaderCard() { return headerCard; }
    public void setHeaderCard(String headerCard) { this.headerCard = headerCard; }

}