package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_archive", schema = "gsi")
public class InventoryArchiveEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invarch_number")
    private Long invarchNumber;

    @Column(name = "location_number", nullable = false)
    private Integer locationNumber;

    @Column(name = "invarch_date", nullable = false)
    private LocalDateTime invarchDate;

    @Column(name = "user_number", nullable = false)
    private Integer userNumber;

    @Column(name = "cash_amt", nullable = false)
    private Long cashAmt;

    public Long getInvarchNumber() { return invarchNumber; }
    public void setInvarchNumber(Long invarchNumber) { this.invarchNumber = invarchNumber; }

    public Integer getLocationNumber() { return locationNumber; }
    public void setLocationNumber(Integer locationNumber) { this.locationNumber = locationNumber; }

    public LocalDateTime getInvarchDate() { return invarchDate; }
    public void setInvarchDate(LocalDateTime invarchDate) { this.invarchDate = invarchDate; }

    public Integer getUserNumber() { return userNumber; }
    public void setUserNumber(Integer userNumber) { this.userNumber = userNumber; }

    public Long getCashAmt() { return cashAmt; }
    public void setCashAmt(Long cashAmt) { this.cashAmt = cashAmt; }
}
