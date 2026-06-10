package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;

import com.CasinoCtC.CCtCAPI.model.TransactionTicketRequest;

public interface TransactionTicketDAO {

    void insertTicketRows(
            Long transactionId,
            List<TransactionTicketRequest> tickets
    );
}