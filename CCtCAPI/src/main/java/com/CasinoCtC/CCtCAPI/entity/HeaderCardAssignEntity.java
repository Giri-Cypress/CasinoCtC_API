package com.CasinoCtC.CCtCAPI.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "header_card_assign", schema = "gsi")
public class HeaderCardAssignEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hc_assign_number")
    private Long hcAssignNumber;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "collection_Date", nullable = false)
    private LocalDate collectionDate;

    @Column(name = "box_number", nullable = false, length = 50)
    private String boxNumber;

    @Column(name = "header_card_ID", nullable = false, length = 20)
    private String headerCardId;

    @Column(name = "file_name", length = 128)
    private String fileName;

    @Column(name = "machine_id")
    private Integer machineId;

    @Column(name = "status")
    private Integer status = 0;

    public Long getHcAssignNumber() { return hcAssignNumber; }
    public void setHcAssignNumber(Long hcAssignNumber) { this.hcAssignNumber = hcAssignNumber; }
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
    public Integer getStatus() { return status == null ? 0 : status; }
    public void setStatus(Integer status) { this.status = status; }
}
