package com.CasinoCtC.CCtCAPI.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;


public class TransactionCurrencyRequest {

    @NotNull(message =
            "Denomination ID is required")
    private Short denomID;

    @NotNull(message =
            "Quality is required")
    private Integer quality;

    @NotBlank(message =
            "Machine ID is required")
    private String machineID;

    @NotNull(message =
            "Count is required")
    @PositiveOrZero(message =
            "Count cannot be negative")
    private Integer count;

    @NotNull(message =
            "Amount is required")
    @PositiveOrZero(message =
            "Amount cannot be negative")
    private Long amount;

    // ✅ Getters and Setters

    public Short getDenomID() {
        return denomID;
    }

    public void setDenomID(
            Short denomID) {

        this.denomID = denomID;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(
            Integer quality) {

        this.quality = quality;
    }

    public String getMachineID() {
        return machineID;
    }

    public void setMachineID(
            String machineID) {

        this.machineID = machineID;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(
            Integer count) {

        this.count = count;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(
            Long amount) {

        this.amount = amount;
    }
}
