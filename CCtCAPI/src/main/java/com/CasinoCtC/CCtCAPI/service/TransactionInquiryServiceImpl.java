package com.CasinoCtC.CCtCAPI.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.dao.TransactionInquiryDAO;
import com.CasinoCtC.CCtCAPI.dto.TransactionInquiryResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionSearchRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionInquiryServiceImpl
implements TransactionInquiryService {

    private final TransactionInquiryDAO transactionInquiryDAO;

    @Override
    public List<TransactionInquiryResponse> searchTransactions(
            TransactionSearchRequest request) {

        return transactionInquiryDAO
            .searchTransactions(request);
    }
}