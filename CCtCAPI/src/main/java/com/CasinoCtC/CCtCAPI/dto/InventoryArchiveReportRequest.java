package com.CasinoCtC.CCtCAPI.dto;

import java.time.LocalDate;

public class InventoryArchiveReportRequest {

    private Integer locationNumberFrom;
    private Integer locationNumberTo;
    private Integer userNumberFrom;
    private Integer userNumberTo;
    private LocalDate archiveDateFrom;
    private LocalDate archiveDateTo;

    public Integer getLocationNumberFrom() { return locationNumberFrom; }
    public void setLocationNumberFrom(Integer locationNumberFrom) { this.locationNumberFrom = locationNumberFrom; }
    public Integer getLocationNumberTo() { return locationNumberTo; }
    public void setLocationNumberTo(Integer locationNumberTo) { this.locationNumberTo = locationNumberTo; }
    public Integer getUserNumberFrom() { return userNumberFrom; }
    public void setUserNumberFrom(Integer userNumberFrom) { this.userNumberFrom = userNumberFrom; }
    public Integer getUserNumberTo() { return userNumberTo; }
    public void setUserNumberTo(Integer userNumberTo) { this.userNumberTo = userNumberTo; }
    public LocalDate getArchiveDateFrom() { return archiveDateFrom; }
    public void setArchiveDateFrom(LocalDate archiveDateFrom) { this.archiveDateFrom = archiveDateFrom; }
    public LocalDate getArchiveDateTo() { return archiveDateTo; }
    public void setArchiveDateTo(LocalDate archiveDateTo) { this.archiveDateTo = archiveDateTo; }
}
