package com.CasinoCtC.CCtCAPI.dto;

import java.util.ArrayList;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class TransactionSaveRequest {
    private TransactionHeaderDto header;
    private List<TransactionCurrencyDto> cash = new ArrayList<>();
    private List<TransactionTicketDto> tickets = new ArrayList<>();
    private Long declaredCashTotal; // cents
    private Integer declaredTicketCount;
    private String headerCard;
    private Integer supervisorOverride;
    private java.util.List<Long> supervisorActivityNumbers = new java.util.ArrayList<>();
    private String status;


    private String headerCardFileName;

    public String getHeaderCardFileName() {return headerCardFileName;}
    public void setHeaderCardFileName(String headerCardFileName) {this.headerCardFileName = headerCardFileName;}
    public TransactionHeaderDto getHeader() { return header; }
    public void setHeader(TransactionHeaderDto header) { this.header = header; }
    public List<TransactionCurrencyDto> getCash() { return cash; }
    public void setCash(List<TransactionCurrencyDto> cash) { this.cash = cash; }
    public List<TransactionTicketDto> getTickets() { return tickets; }
    public void setTickets(List<TransactionTicketDto> tickets) { this.tickets = tickets; }
    public Long getDeclaredCashTotal() { return declaredCashTotal; }
    public void setDeclaredCashTotal(Long declaredCashTotal) { this.declaredCashTotal = declaredCashTotal; }
    public Integer getDeclaredTicketCount() { return declaredTicketCount; }
    public void setDeclaredTicketCount(Integer declaredTicketCount) { this.declaredTicketCount = declaredTicketCount; }
    public Integer getSupervisorOverride() { return supervisorOverride; }
    public void setSupervisorOverride(Integer supervisorOverride) { this.supervisorOverride = supervisorOverride; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHeaderCard() { return headerCard; }
    public void setHeaderCard(String headerCard) { this.headerCard = headerCard; }

    public java.util.List<Long> getSupervisorActivityNumbers() { return supervisorActivityNumbers; }
    public void setSupervisorActivityNumbers(java.util.List<Long> supervisorActivityNumbers) { this.supervisorActivityNumbers = supervisorActivityNumbers; }
}
