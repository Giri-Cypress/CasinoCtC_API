package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_param_values", schema = "gsi")
public class TransactionParamValueEntity {

    @EmbeddedId
    private TransactionParamValueId id;

    @Column(name = "param_value", nullable = false, length = 255)
    private String paramValue;

    public TransactionParamValueId getId() { return id; }
    public void setId(TransactionParamValueId id) { this.id = id; }

    public String getParamValue() { return paramValue; }
    public void setParamValue(String paramValue) { this.paramValue = paramValue; }
}
