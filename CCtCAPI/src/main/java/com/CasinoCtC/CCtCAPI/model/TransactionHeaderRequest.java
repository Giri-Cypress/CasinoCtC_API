package com.CasinoCtC.CCtCAPI.model;

import java.time.LocalDate;
import java.util.List;

public class TransactionHeaderRequest {

    private LocalDate businessDate;
    private LocalDate collectionDate;
    private String boxNumber;
    private String workstationId;
    private String processUser;
    private List<TransactionParameter> parameters;

    public LocalDate getBusinessDate() { return businessDate; }
    public void setBusinessDate(LocalDate businessDate) { this.businessDate = businessDate; }
    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }
    public String getBoxNumber() { return boxNumber; }
    public void setBoxNumber(String boxNumber) { this.boxNumber = boxNumber; }


    public String getWorkstationId() { return workstationId; }
    public void setWorkstationId(String workstationId) { this.workstationId = workstationId; }
    public String getProcessUser() { return processUser; }
    public void setProcessUser(String processUser) { this.processUser = processUser; }
    public List<TransactionParameter> getParameters() { return parameters; }
    public void setParameters(List<TransactionParameter> parameters) { this.parameters = parameters; }
}
