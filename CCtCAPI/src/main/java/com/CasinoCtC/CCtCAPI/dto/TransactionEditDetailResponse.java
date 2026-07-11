package com.CasinoCtC.CCtCAPI.dto;

import java.util.ArrayList;
import java.util.List;

public class TransactionEditDetailResponse {
    private Long transNumber;
    private Integer locationNumber;
    private Integer userNumber;
    private String userName;
    private String status;
    private String headerCard;
    private TransactionHeaderDto header;
    private List<TransactionCurrencyDto> cash = new ArrayList<>();
    private List<TransactionTicketDto> tickets = new ArrayList<>();
    private List<TransactionEditParamDto> parameters = new ArrayList<>();
    private TransactionSaveResponse summary;

    public Long getTransNumber() { return transNumber; }
    public void setTransNumber(Long transNumber) { this.transNumber = transNumber; }
    public Integer getLocationNumber() { return locationNumber; }
    public void setLocationNumber(Integer locationNumber) { this.locationNumber = locationNumber; }
    public Integer getUserNumber() { return userNumber; }
    public void setUserNumber(Integer userNumber) { this.userNumber = userNumber; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public TransactionHeaderDto getHeader() { return header; }
    public void setHeader(TransactionHeaderDto header) { this.header = header; }
    public List<TransactionCurrencyDto> getCash() { return cash; }
    public void setCash(List<TransactionCurrencyDto> cash) { this.cash = cash; }
    public List<TransactionTicketDto> getTickets() { return tickets; }
    public void setTickets(List<TransactionTicketDto> tickets) { this.tickets = tickets; }
    public List<TransactionEditParamDto> getParameters() { return parameters; }
    public void setParameters(List<TransactionEditParamDto> parameters) { this.parameters = parameters; }
    public TransactionSaveResponse getSummary() { return summary; }
    public void setSummary(TransactionSaveResponse summary) { this.summary = summary; }
    public String getHeaderCard() { return headerCard; }
    public void setHeaderCard(String headerCard) { this.headerCard = headerCard; }

}
