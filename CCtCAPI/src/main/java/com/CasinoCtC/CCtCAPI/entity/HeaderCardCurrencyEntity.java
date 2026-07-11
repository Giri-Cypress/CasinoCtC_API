package com.CasinoCtC.CCtCAPI.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "header_card_currency", schema = "gsi")
@IdClass(HeaderCardCurrencyId.class)
public class HeaderCardCurrencyEntity {

    @Id
    @Column(name = "hc_assign_number")
    private Long hcAssignNumber;

    @Id
    @Column(name = "denom_number")
    private Short denomNumber;

    @Id
    @Column(name = "quality")
    private Integer quality;

    @Column(name = "machine_id", length = 48)
    private String machineId;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "count_machine", nullable = false)
    private Integer countMachine;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    public Long getHcAssignNumber() { return hcAssignNumber; }
    public void setHcAssignNumber(Long hcAssignNumber) { this.hcAssignNumber = hcAssignNumber; }
    public Short getDenomNumber() { return denomNumber; }
    public void setDenomNumber(Short denomNumber) { this.denomNumber = denomNumber; }
    public Integer getQuality() { return quality; }
    public void setQuality(Integer quality) { this.quality = quality; }
    public String getMachineId() { return machineId; }
    public void setMachineId(String machineId) { this.machineId = machineId; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public Integer getCountMachine() { return countMachine; }
    public void setCountMachine(Integer countMachine) { this.countMachine = countMachine; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
