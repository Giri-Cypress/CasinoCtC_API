package com.CasinoCtC.CCtCAPI.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TransactionHeaderDetailResponse {

    private Integer transNumber;
    private LocalDate businessDate;
    private LocalDate collectionDate;
    private String boxNumber;
    private String processUser;
    private String status;
}