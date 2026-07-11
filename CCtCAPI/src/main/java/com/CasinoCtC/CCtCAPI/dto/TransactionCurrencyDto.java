package com.CasinoCtC.CCtCAPI.dto;

public class TransactionCurrencyDto {
    // denomination is denomination.denom_number, not the displayed bill/coin value.
    private Integer denomination;
    private String description;
    private Long denomValue; // cents
    private Integer count;
    private Integer machineCount;
    private Long amount; // cents

    public Integer getDenomination() { return denomination; }
    public void setDenomination(Integer denomination) { this.denomination = denomination; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getDenomValue() { return denomValue; }
    public void setDenomValue(Long denomValue) { this.denomValue = denomValue; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public Integer getMachineCount() { return machineCount; }
    public void setMachineCount(Integer machineCount) { this.machineCount = machineCount; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
}
