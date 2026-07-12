package com.CasinoCtC.CCtCAPI.model;

public class CollectionPoint {

    private Integer collectionPointId;
    private Integer locationNumber;
    private String collectionPointName;
    private String description;
    private Integer status;
    private Integer cpType;

    public Integer getCollectionPointId() {
        return collectionPointId;
    }

    public void setCollectionPointId(Integer collectionPointId) {
        this.collectionPointId = collectionPointId;
    }

    public Integer getLocationNumber() {
        return locationNumber;
    }

    public void setLocationNumber(Integer locationNumber) {
        this.locationNumber = locationNumber;
    }

    public String getCollectionPointName() {
        return collectionPointName;
    }

    public void setCollectionPointName(String collectionPointName) {
        this.collectionPointName = collectionPointName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCpType() {
        return cpType;
    }

    public void setCpType(Integer cpType) {
        this.cpType = cpType;
    }
}