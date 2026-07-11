package com.CasinoCtC.CCtCAPI.dto;

public class HeaderCardCurrencyCountDto {
    private String currency;
    private Long denomValue;
    private Integer count;

    public HeaderCardCurrencyCountDto() {}

    public HeaderCardCurrencyCountDto(String currency, Long denomValue, Integer count) {
        this.currency = currency;
        this.denomValue = denomValue;
        this.count = count;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Long getDenomValue() { return denomValue; }
    public void setDenomValue(Long denomValue) { this.denomValue = denomValue; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}
