package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TransactionCurrencyId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "trans_number")
    private Long transNumber;

    @Column(name = "denom_number")
    private Integer denomNumber;

    @Column(name = "quality")
    private Integer quality;

    @Column(name = "machine_id")
    private String machineId;

    public TransactionCurrencyId() {
    }

    public TransactionCurrencyId(Long transNumber, Integer denomNumber, Integer quality, String machineId) {
        this.transNumber = transNumber;
        this.denomNumber = denomNumber;
        this.quality = quality;
        this.machineId = machineId;
    }

    public Long getTransNumber() { return transNumber; }
    public void setTransNumber(Long transNumber) { this.transNumber = transNumber; }

    public Integer getDenomNumber() { return denomNumber; }
    public void setDenomNumber(Integer denomNumber) { this.denomNumber = denomNumber; }

    public Integer getQuality() { return quality; }
    public void setQuality(Integer quality) { this.quality = quality; }

    public String getMachineId() { return machineId; }
    public void setMachineId(String machineId) { this.machineId = machineId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionCurrencyId)) return false;
        TransactionCurrencyId that = (TransactionCurrencyId) o;
        return Objects.equals(transNumber, that.transNumber)
                && Objects.equals(denomNumber, that.denomNumber)
                && Objects.equals(quality, that.quality)
                && Objects.equals(machineId, that.machineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transNumber, denomNumber, quality, machineId);
    }
}
