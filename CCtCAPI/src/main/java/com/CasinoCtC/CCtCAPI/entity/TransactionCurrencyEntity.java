package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_currency", schema = "gsi")
public class TransactionCurrencyEntity {
    @EmbeddedId
    private TransactionCurrencyId id;

    @Column(name = "count", nullable = false)
    private Integer count;    @Column(name = "count_machine", nullable = false)
    private Integer countMachine;



    @Column(name = "amount", nullable = false)
    private Long amount; // cents

    public TransactionCurrencyId getId() { return id; }
    public void setId(TransactionCurrencyId id) { this.id = id; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public Integer getCountMachine() { return countMachine; }
    public void setCountMachine(Integer countMachine) { this.countMachine = countMachine; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public Long getTransNumber() { return id != null ? id.getTransNumber() : null; }
    public Integer getDenomNumber() { return id != null ? id.getDenomNumber() : null; }
    public Integer getQuality() { return id != null ? id.getQuality() : null; }
    public String getMachineId() { return id != null ? id.getMachineId() : null; }
}
