package com.CasinoCtC.CCtCAPI.service;

import com.CasinoCtC.CCtCAPI.dto.TransactionRequest;

public interface TransactionService {

    void saveTransaction(
            TransactionRequest request,
            String username
    );
    Object getTransactionById(Long id);
}