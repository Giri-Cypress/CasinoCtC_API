package com.CasinoCtC.CCtCAPI.dto;

public class TransactionEditParamDto {
    private Integer paramNumber;
    private String paramName;
    private String paramLabel;
    private Integer paramRequired;
    private Integer paramSize;
    private String validationProc;
    private String value;

    public Integer getParamNumber() { return paramNumber; }
    public void setParamNumber(Integer paramNumber) { this.paramNumber = paramNumber; }
    public String getParamName() { return paramName; }
    public void setParamName(String paramName) { this.paramName = paramName; }
    public String getParamLabel() { return paramLabel; }
    public void setParamLabel(String paramLabel) { this.paramLabel = paramLabel; }
    public Integer getParamRequired() { return paramRequired; }
    public void setParamRequired(Integer paramRequired) { this.paramRequired = paramRequired; }
    public Integer getParamSize() { return paramSize; }
    public void setParamSize(Integer paramSize) { this.paramSize = paramSize; }
    public String getValidationProc() { return validationProc; }
    public void setValidationProc(String validationProc) { this.validationProc = validationProc; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
