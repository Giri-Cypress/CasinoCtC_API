package com.CasinoCtC.CCtCAPI.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_dtl", schema = "gsi")
public class InventoryDtlEntity {

    @EmbeddedId
    private InventoryDtlId id;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public InventoryDtlId getId() {
        return id;
    }

    public void setId(InventoryDtlId id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
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

    public Integer getDenomNumber() {
        return id != null ? id.getDenomNumber() : null;
    }
}
