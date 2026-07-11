package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trans_params_config", schema = "gsi")
public class TransParamConfigEntity {

    @Id
    @Column(name = "param_number")
    private Integer paramNumber;

    @Column(name = "param_name")
    private String paramName;

    @Column(name = "param_label")
    private String paramLabel;

    @Column(name = "param_inuse")
    private Integer paramInuse;

    @Column(name = "param_required")
    private Integer paramRequired;

    @Column(name = "param_size")
    private Integer paramSize;

    @Column(name = "validation_proc")
    private String validationProc;

    @Column(name = "display_order")
    private Integer displayOrder;

    public Integer getParamNumber() { return paramNumber; }
    public void setParamNumber(Integer paramNumber) { this.paramNumber = paramNumber; }
    public String getParamName() { return paramName; }
    public void setParamName(String paramName) { this.paramName = paramName; }
    public String getParamLabel() { return paramLabel; }
    public void setParamLabel(String paramLabel) { this.paramLabel = paramLabel; }
    public Integer getParamInuse() { return paramInuse; }
    public void setParamInuse(Integer paramInuse) { this.paramInuse = paramInuse; }
    public Integer getParamRequired() { return paramRequired; }
    public void setParamRequired(Integer paramRequired) { this.paramRequired = paramRequired; }
    public Integer getParamSize() { return paramSize; }
    public void setParamSize(Integer paramSize) { this.paramSize = paramSize; }
    public String getValidationProc() { return validationProc; }
    public void setValidationProc(String validationProc) { this.validationProc = validationProc; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
