package com.CasinoCtC.CCtCAPI.dto;

import java.util.ArrayList;
import java.util.List;

public class TransactionEditUpdateRequest {
    private TransactionHeaderDto header;
    private List<TransactionCurrencyDto> cash = new ArrayList<>();
    private List<TransactionTicketDto> tickets = new ArrayList<>();
    private List<TransactionEditParamDto> parameters = new ArrayList<>();
    private Long declaredCashTotal;
    private Integer declaredTicketCount;
    private Integer supervisorOverride;
    private String status;

    public TransactionHeaderDto getHeader() { return header; }
    public void setHeader(TransactionHeaderDto header) { this.header = header; }
    public List<TransactionCurrencyDto> getCash() { return cash; }
    public void setCash(List<TransactionCurrencyDto> cash) { this.cash = cash; }
    public List<TransactionTicketDto> getTickets() { return tickets; }
    public void setTickets(List<TransactionTicketDto> tickets) { this.tickets = tickets; }
    public List<TransactionEditParamDto> getParameters() { return parameters; }
    public void setParameters(List<TransactionEditParamDto> parameters) { this.parameters = parameters; }
    public Long getDeclaredCashTotal() { return declaredCashTotal; }
    public void setDeclaredCashTotal(Long declaredCashTotal) { this.declaredCashTotal = declaredCashTotal; }
    public Integer getDeclaredTicketCount() { return declaredTicketCount; }
    public void setDeclaredTicketCount(Integer declaredTicketCount) { this.declaredTicketCount = declaredTicketCount; }
    public Integer getSupervisorOverride() { return supervisorOverride; }
    public void setSupervisorOverride(Integer supervisorOverride) { this.supervisorOverride = supervisorOverride; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
