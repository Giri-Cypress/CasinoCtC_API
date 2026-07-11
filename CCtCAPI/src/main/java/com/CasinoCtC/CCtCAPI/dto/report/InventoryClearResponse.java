package com.CasinoCtC.CCtCAPI.dto.report;

public class InventoryClearResponse {
    private int archivedHeaders;
    private int archivedDetails;
    private int clearedHeaders;
    private int clearedDetails;
    private String message;

    public InventoryClearResponse() {}

    public InventoryClearResponse(int archivedHeaders, int archivedDetails, int clearedHeaders, int clearedDetails, String message) {
        this.archivedHeaders = archivedHeaders;
        this.archivedDetails = archivedDetails;
        this.clearedHeaders = clearedHeaders;
        this.clearedDetails = clearedDetails;
        this.message = message;
    }

    public int getArchivedHeaders() { return archivedHeaders; }
    public void setArchivedHeaders(int archivedHeaders) { this.archivedHeaders = archivedHeaders; }

    public int getArchivedDetails() { return archivedDetails; }
    public void setArchivedDetails(int archivedDetails) { this.archivedDetails = archivedDetails; }

    public int getClearedHeaders() { return clearedHeaders; }
    public void setClearedHeaders(int clearedHeaders) { this.clearedHeaders = clearedHeaders; }

    public int getClearedDetails() { return clearedDetails; }
    public void setClearedDetails(int clearedDetails) { this.clearedDetails = clearedDetails; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
