package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;

import com.CasinoCtC.CCtCAPI.model.TransactionCurrencyRequest;

public interface InventoryDAO {

    void updateInventory(
            Integer locationId,
            Integer userId,
            Long totalCurrency
    );

    void updateInventoryDetails(
            Integer locationId,
            Integer userId,
            List<TransactionCurrencyRequest> currencyRows
    );
}