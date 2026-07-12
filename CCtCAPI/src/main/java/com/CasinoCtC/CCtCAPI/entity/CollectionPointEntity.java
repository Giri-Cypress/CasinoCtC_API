package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Table(name = "collection_point", schema = "gsi")
@Data
public class CollectionPointEntity {

    @Id
    @Column(name = "collection_point_id")
    private Integer collectionPointId;

    @Column(name = "location_number")
    private Integer locationNumber;

    @Column(name = "collection_point_name")
    private String collectionPointName;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Integer status;

    @Column(name = "cp_type")
    private Integer cpType;
}