package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;

import com.CasinoCtC.CCtCAPI.model.TransactionCurrencyRequest;

public interface TransactionCurrencyDAO {

    void insertCurrencyRows(
            Long transactionId,
            List<TransactionCurrencyRequest> currencyRows
    );
}