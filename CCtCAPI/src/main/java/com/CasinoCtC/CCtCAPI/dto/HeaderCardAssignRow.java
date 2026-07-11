package com.CasinoCtC.CCtCAPI.dto;

import java.time.LocalDate;

public class HeaderCardAssignRow {
    private String headerCardId;
    private LocalDate businessDate;
    private LocalDate collectionDate;
    private String boxNumber;
    private String fileName;
    private Integer status;
    private LocalDate processDate;

    public String getHeaderCardId() { return headerCardId; }
    public void setHeaderCardId(String headerCardId) { this.headerCardId = headerCardId; }

    public LocalDate getBusinessDate() { return businessDate; }
    public void setBusinessDate(LocalDate businessDate) { this.businessDate = businessDate; }

    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }

    public String getBoxNumber() { return boxNumber; }
    public void setBoxNumber(String boxNumber) { this.boxNumber = boxNumber; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDate getProcessDate() { return processDate; }
    public void setProcessDate(LocalDate processDate) { this.processDate = processDate; }
}
