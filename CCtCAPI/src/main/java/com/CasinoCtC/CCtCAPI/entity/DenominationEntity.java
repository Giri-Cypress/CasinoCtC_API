package com.CasinoCtC.CCtCAPI.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "denomination", schema = "gsi")
public class DenominationEntity {
    @Id
    @Column(name = "denom_number")
    private Integer denomNumber;

    @Column(name = "denom_value", nullable = false)
    private Long denomValue; // cents

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Column(name = "active", nullable = false)
    private Integer active;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Integer getDenomNumber() { return denomNumber; }
    public void setDenomNumber(Integer denomNumber) { this.denomNumber = denomNumber; }
    public Long getDenomValue() { return denomValue; }
    public void setDenomValue(Long denomValue) { this.denomValue = denomValue; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getActive() { return active; }
    public void setActive(Integer active) { this.active = active; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
