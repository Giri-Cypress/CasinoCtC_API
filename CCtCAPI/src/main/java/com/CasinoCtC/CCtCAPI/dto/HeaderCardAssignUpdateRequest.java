package com.CasinoCtC.CCtCAPI.dto;

import java.time.LocalDate;

public class HeaderCardAssignUpdateRequest {
    private String headerCardId;
    private LocalDate businessDate;
    private LocalDate collectionDate;
    private String boxNumber;

    public String getHeaderCardId() { return headerCardId; }
    public void setHeaderCardId(String headerCardId) { this.headerCardId = headerCardId; }

    public LocalDate getBusinessDate() { return businessDate; }
    public void setBusinessDate(LocalDate businessDate) { this.businessDate = businessDate; }

    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }

    public String getBoxNumber() { return boxNumber; }
    public void setBoxNumber(String boxNumber) { this.boxNumber = boxNumber; }
}
