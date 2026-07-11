package com.CasinoCtC.CCtCAPI.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TransactionParamValueId implements Serializable {

    @Column(name = "trans_number")
    private Integer transNumber;

    @Column(name = "param_number")
    private Integer paramNumber;

    public Integer getTransNumber() { return transNumber; }
    public void setTransNumber(Integer transNumber) { this.transNumber = transNumber; }

    public Integer getParamNumber() { return paramNumber; }
    public void setParamNumber(Integer paramNumber) { this.paramNumber = paramNumber; }
}
