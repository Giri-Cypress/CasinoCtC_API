package com.CasinoCtC.CCtCAPI.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionDetailResponse {

    private Long transNumber;
    private String userName;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("declared_cash_total")
    private Long declaredCashTotal;

    @JsonProperty("declared_ticket_count")
    private Integer declaredTicketCount;
    private String headerCard;

    @JsonProperty("header_card_file")
    private String headerCardFile;

    @JsonProperty("cash_difference")
    private Long cashDifference;

    @JsonProperty("supervisor_override_user_number")
    private Integer supervisorOverrideUserNumber;

    @JsonProperty("supervisor_name")
    private String supervisorName;

    private TransactionHeaderDto header;
    private List<TransactionCurrencyDto> cash = new ArrayList<>();
    private List<TransactionTicketDto> tickets = new ArrayList<>();
    private TransactionSaveResponse summary;

    public Long getTransNumber() { return transNumber; }
    public void setTransNumber(Long transNumber) { this.transNumber = transNumber; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public Long getDeclaredCashTotal() { return declaredCashTotal; }
    public void setDeclaredCashTotal(Long declaredCashTotal) { this.declaredCashTotal = declaredCashTotal; }

    public Integer getDeclaredTicketCount() { return declaredTicketCount; }
    public void setDeclaredTicketCount(Integer declaredTicketCount) { this.declaredTicketCount = declaredTicketCount; }

    public Long getCashDifference() { return cashDifference; }
    public void setCashDifference(Long cashDifference) { this.cashDifference = cashDifference; }

    public Integer getSupervisorOverrideUserNumber() { return supervisorOverrideUserNumber; }
    public void setSupervisorOverrideUserNumber(Integer supervisorOverrideUserNumber) { this.supervisorOverrideUserNumber = supervisorOverrideUserNumber; }

    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }

    public TransactionHeaderDto getHeader() { return header; }
    public void setHeader(TransactionHeaderDto header) { this.header = header; }

    public List<TransactionCurrencyDto> getCash() { return cash; }
    public void setCash(List<TransactionCurrencyDto> cash) { this.cash = cash; }

    public List<TransactionTicketDto> getTickets() { return tickets; }
    public void setTickets(List<TransactionTicketDto> tickets) { this.tickets = tickets; }

    public TransactionSaveResponse getSummary() { return summary; }
    public void setSummary(TransactionSaveResponse summary) { this.summary = summary; }
    public String getHeaderCard() { return headerCard; }
    public void setHeaderCard(String headerCard) { this.headerCard = headerCard; }

    public String getHeaderCardFile() { return headerCardFile; }
    public void setHeaderCardFile(String headerCardFile) { this.headerCardFile = headerCardFile; }

}
