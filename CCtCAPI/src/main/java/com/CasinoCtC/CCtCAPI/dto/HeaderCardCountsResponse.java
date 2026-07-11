package com.CasinoCtC.CCtCAPI.dto;

import java.util.ArrayList;
import java.util.List;

public class HeaderCardCountsResponse {
    private boolean found;
    private String fileName;
    private String message;
    private List<HeaderCardCurrencyCountDto> currencyCounts = new ArrayList<>();
    private List<String> ticketIds = new ArrayList<>();

    public boolean isFound() { return found; }
    public void setFound(boolean found) { this.found = found; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<HeaderCardCurrencyCountDto> getCurrencyCounts() { return currencyCounts; }
    public void setCurrencyCounts(List<HeaderCardCurrencyCountDto> currencyCounts) {
        this.currencyCounts = currencyCounts == null ? new ArrayList<>() : currencyCounts;
    }
    public List<String> getTicketIds() { return ticketIds; }
    public void setTicketIds(List<String> ticketIds) {
        this.ticketIds = ticketIds == null ? new ArrayList<>() : ticketIds;
    }
}
