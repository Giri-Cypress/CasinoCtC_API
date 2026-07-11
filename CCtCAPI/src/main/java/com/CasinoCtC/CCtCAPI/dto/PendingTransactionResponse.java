package com.CasinoCtC.CCtCAPI.dto;

public class PendingTransactionResponse {

    private Long transNumber;
    private String businessDate;
    private String collectionDate;
    private String boxNumber;
    private String status;

    public Long getTransNumber() {
        return transNumber;
    }

    public void setTransNumber(Long transNumber) {
        this.transNumber = transNumber;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}