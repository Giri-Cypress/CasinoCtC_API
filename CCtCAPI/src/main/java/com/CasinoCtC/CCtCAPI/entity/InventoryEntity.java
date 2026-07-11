package com.CasinoCtC.CCtCAPI.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory", schema = "gsi")
public class InventoryEntity {

    @EmbeddedId
    private InventoryId id;

    @Column(name = "cash_amt", nullable = false)
    private Long cashAmt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public InventoryId getId() {
        return id;
    }

    public void setId(InventoryId id) {
        this.id = id;
    }

    public Long getCashAmt() {
        return cashAmt;
    }

    public void setCashAmt(Long cashAmt) {
        this.cashAmt = cashAmt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getLocationNumber() {
        return id != null ? id.getLocationNumber() : null;
    }

    public Integer getUserNumber() {
        return id != null ? id.getUserNumber() : null;
    }
}
