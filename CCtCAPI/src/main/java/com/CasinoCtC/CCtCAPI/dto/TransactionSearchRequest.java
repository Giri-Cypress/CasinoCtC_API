package com.CasinoCtC.CCtCAPI.dto;

import java.time.LocalDate;

public class TransactionSearchRequest {

    private LocalDate businessDateFrom;
    private LocalDate businessDateTo;
    private LocalDate collectionDateFrom;
    private LocalDate collectionDateTo;

    private String boxNumberFrom;
    private String boxNumberTo;

    private Integer employeeNumber;
    private String ticketNumber;
    private String status;

    public LocalDate getBusinessDateFrom() {
        return businessDateFrom;
    }

    public void setBusinessDateFrom(LocalDate businessDateFrom) {
        this.businessDateFrom = businessDateFrom;
    }

    public LocalDate getBusinessDateTo() {
        return businessDateTo;
    }

    public void setBusinessDateTo(LocalDate businessDateTo) {
        this.businessDateTo = businessDateTo;
    }

    public LocalDate getCollectionDateFrom() {
        return collectionDateFrom;
    }

    public void setCollectionDateFrom(LocalDate collectionDateFrom) {
        this.collectionDateFrom = collectionDateFrom;
    }

    public LocalDate getCollectionDateTo() {
        return collectionDateTo;
    }

    public void setCollectionDateTo(LocalDate collectionDateTo) {
        this.collectionDateTo = collectionDateTo;
    }

    public String getBoxNumberFrom() {
        return boxNumberFrom;
    }

    public void setBoxNumberFrom(String boxNumberFrom) {
        this.boxNumberFrom = boxNumberFrom;
    }

    public String getBoxNumberTo() {
        return boxNumberTo;
    }

    public void setBoxNumberTo(String boxNumberTo) {
        this.boxNumberTo = boxNumberTo;
    }

    public Integer getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(Integer employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}