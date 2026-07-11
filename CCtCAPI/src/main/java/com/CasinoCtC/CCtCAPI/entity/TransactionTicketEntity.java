package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_ticket", schema = "gsi")
public class TransactionTicketEntity {
    @EmbeddedId
    private TransactionTicketId id;

    @Column(name = "amount", nullable = false)
    private Long amount; // cents

    public TransactionTicketId getId() { return id; }
    public void setId(TransactionTicketId id) { this.id = id; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public Long getTransNumber() { return id != null ? id.getTransNumber() : null; }
    public String getTicketId() { return id != null ? id.getTicketId() : null; }
}
