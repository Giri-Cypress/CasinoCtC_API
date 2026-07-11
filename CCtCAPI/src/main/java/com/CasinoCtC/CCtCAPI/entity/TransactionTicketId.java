package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TransactionTicketId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "trans_number")
    private Long transNumber;

    @Column(name = "ticket_id")
    private String ticketId;

    public TransactionTicketId() {
    }

    public TransactionTicketId(Long transNumber, String ticketId) {
        this.transNumber = transNumber;
        this.ticketId = ticketId;
    }

    public Long getTransNumber() { return transNumber; }
    public void setTransNumber(Long transNumber) { this.transNumber = transNumber; }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionTicketId)) return false;
        TransactionTicketId that = (TransactionTicketId) o;
        return Objects.equals(transNumber, that.transNumber) && Objects.equals(ticketId, that.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transNumber, ticketId);
    }
}
