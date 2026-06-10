package com.CasinoCtC.CCtCAPI.dao;

import com.CasinoCtC.CCtCAPI.dto.TransactionRequest;

public interface TransactionDAO {

    Long saveTransaction(
            TransactionRequest request,
            Integer userId,
            Integer locationId
    );
    Object getTransactionById(Long id);
}