package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(InventoryArchiveDtlId.class)
@Table(name = "inventory_archive_dtl", schema = "gsi")
public class InventoryArchiveDtlEntity {
    @Id
    @Column(name = "invarch_number")
    private Long invarchNumber;

    @Id
    @Column(name = "location_number")
    private Integer locationNumber;

    @Id
    @Column(name = "denom_number")
    private Integer denomNumber;

    @Column(name = "amount", nullable = false)
    private Long amount;

    public Long getInvarchNumber() { return invarchNumber; }
    public void setInvarchNumber(Long invarchNumber) { this.invarchNumber = invarchNumber; }

    public Integer getLocationNumber() { return locationNumber; }
    public void setLocationNumber(Integer locationNumber) { this.locationNumber = locationNumber; }

    public Integer getDenomNumber() { return denomNumber; }
    public void setDenomNumber(Integer denomNumber) { this.denomNumber = denomNumber; }

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
}
