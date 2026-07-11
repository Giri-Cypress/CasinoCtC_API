package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import java.util.Objects;

public class HeaderCardCurrencyId implements Serializable {
    private Long hcAssignNumber;
    private Short denomNumber;
    private Integer quality;

    public HeaderCardCurrencyId() {}
    public HeaderCardCurrencyId(Long hcAssignNumber, Short denomNumber, Integer quality) {
        this.hcAssignNumber = hcAssignNumber;
        this.denomNumber = denomNumber;
        this.quality = quality;
    }

    public Long getHcAssignNumber() { return hcAssignNumber; }
    public void setHcAssignNumber(Long hcAssignNumber) { this.hcAssignNumber = hcAssignNumber; }
    public Short getDenomNumber() { return denomNumber; }
    public void setDenomNumber(Short denomNumber) { this.denomNumber = denomNumber; }
    public Integer getQuality() { return quality; }
    public void setQuality(Integer quality) { this.quality = quality; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HeaderCardCurrencyId)) return false;
        HeaderCardCurrencyId that = (HeaderCardCurrencyId) o;
        return Objects.equals(hcAssignNumber, that.hcAssignNumber)
            && Objects.equals(denomNumber, that.denomNumber)
            && Objects.equals(quality, that.quality);
    }
    @Override public int hashCode() { return Objects.hash(hcAssignNumber, denomNumber, quality); }
}
