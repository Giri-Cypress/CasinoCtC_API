package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;
import java.util.Map;

import com.CasinoCtC.CCtCAPI.dto.TransactionInquiryResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionSearchRequest;

public interface TransactionInquiryDAO {

    /*
     * =========================================
     * SEARCH TRANSACTIONS (Inquiry Screen)
     * =========================================
     */
    List<TransactionInquiryResponse> searchTransactions(
        TransactionSearchRequest request
    );

    /*
     * =========================================
     * GET TRANSACTION BY ID (View Screen)
     * =========================================
     */
    Map<String, Object> getTransactionById(Long id);
}