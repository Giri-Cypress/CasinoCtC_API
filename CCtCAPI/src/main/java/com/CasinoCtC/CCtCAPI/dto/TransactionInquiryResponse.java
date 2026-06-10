package com.CasinoCtC.CCtCAPI.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionInquiryResponse {

    private Long transactionId;

    private String businessDate;

    private String collectionDate;

    private String boxNumber;

    private String username;

    private String locationName;

    private String status;

    private BigDecimal totalCurrency;

    private BigDecimal totalTktAmount;

    private BigDecimal totalAmount;

    private Date createdDate;
}