package com.CasinoCtC.CCtCAPI.dto;

import java.time.LocalDate;

public class HeaderCardAssignRequest {
    private LocalDate businessDate;
    private LocalDate collectionDate;
    private String boxNumber;
    private String headerCardId;
    private String fileName;
    private Integer machineId;

    public LocalDate getBusinessDate() { return businessDate; }
    public void setBusinessDate(LocalDate businessDate) { this.businessDate = businessDate; }
    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }
    public String getBoxNumber() { return boxNumber; }
    public void setBoxNumber(String boxNumber) { this.boxNumber = boxNumber; }
    public String getHeaderCardId() { return headerCardId; }
    public void setHeaderCardId(String headerCardId) { this.headerCardId = headerCardId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Integer getMachineId() { return machineId; }
    public void setMachineId(Integer machineId) { this.machineId = machineId; }
}
