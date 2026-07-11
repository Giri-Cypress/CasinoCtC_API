package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import java.util.Map;

import com.CasinoCtC.CCtCAPI.dto.PendingTransactionResponse;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardCountsResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionDetailResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionEditDetailResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionEditUpdateRequest;
import com.CasinoCtC.CCtCAPI.dto.TransactionInquiryRowResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionSaveRequest;
import com.CasinoCtC.CCtCAPI.dto.TransactionSaveResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionSearchRequest;

public interface TransactionService {

    TransactionSaveResponse saveTransaction(
            TransactionSaveRequest request,
            String userName);

    TransactionDetailResponse getTransactionByNumber(
            Long transNumber);

    // ✅ NEW
    List<TransactionInquiryRowResponse> searchTransactions(
            TransactionSearchRequest request);

    PendingTransactionResponse getPendingTransaction(
            String userName);

    void cancelTransaction(Long transNumber);

    TransactionEditDetailResponse getTransactionForEdit(Long transNumber);

    TransactionSaveResponse updateTransaction(Long transNumber, TransactionEditUpdateRequest request, String userName);

    boolean isUseHeaderCardEnabled(String userName);

    Map<String, Object> getHeaderCardSettings(String userName);

    HeaderCardCountsResponse getHeaderCardCounts(String userName, String headerCard);

    HeaderCardCountsResponse getDeviceTicketFtpCounts(String userName, String ticketFtpId);
}
