package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.CasinoCtC.CCtCAPI.dto.PendingTransactionResponse;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardCountsRequest;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardCountsResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionDetailResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionEditUpdateRequest;
import com.CasinoCtC.CCtCAPI.dto.TransactionEditDetailResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionInquiryRowResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionSaveRequest;
import com.CasinoCtC.CCtCAPI.dto.TransactionSaveResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionSearchRequest;
import com.CasinoCtC.CCtCAPI.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public TransactionSaveResponse saveTransaction(
            @RequestBody TransactionSaveRequest request,
            Authentication authentication) {
        return transactionService.saveTransaction(
                request,
                authentication.getName());
    }

    // ✅ NEW
    @PostMapping("/search")
    public List<TransactionInquiryRowResponse> searchTransactions(
            @RequestBody TransactionSearchRequest request) {
        return transactionService.searchTransactions(request);
    }

    @GetMapping("/use-headercard")
    public Map<String, Boolean> isUseHeaderCardEnabled(Authentication authentication) {
        return Map.of("enabled", transactionService.isUseHeaderCardEnabled(authentication.getName()));
    }

    @GetMapping("/header-card-settings")
    public Map<String, Object> getHeaderCardSettings(Authentication authentication) {
        return transactionService.getHeaderCardSettings(authentication.getName());
    }

    @PostMapping("/header-card-counts")
    public HeaderCardCountsResponse getHeaderCardCounts(
            @RequestBody HeaderCardCountsRequest request,
            Authentication authentication) {
        return transactionService.getHeaderCardCounts(authentication.getName(), request.getHeaderCard());
    }

    @PostMapping("/device-ticket-ftp-counts")
    public HeaderCardCountsResponse getDeviceTicketFtpCounts(
            @RequestBody HeaderCardCountsRequest request,
            Authentication authentication) {
        return transactionService.getDeviceTicketFtpCounts(authentication.getName(), request.getTicketFtpId());
    }

    @GetMapping("/pending")
    public PendingTransactionResponse getPendingTransaction(
            Authentication authentication) {

        return transactionService.getPendingTransaction(
                authentication.getName());
    }

    @PutMapping("/{transNumber}/cancel")
    public void cancelTransaction(
            @PathVariable Long transNumber) {

        transactionService.cancelTransaction(transNumber);
    }


    @GetMapping("/edit/{transNumber}")
    public TransactionEditDetailResponse getTransactionForEdit(
            @PathVariable Long transNumber) {
        if (transNumber == null || transNumber <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Valid transaction number is required");
        }
        return transactionService.getTransactionForEdit(transNumber);
    }

    @PutMapping("/edit/{transNumber}")
    public TransactionSaveResponse updateTransaction(
            @PathVariable Long transNumber,
            @RequestBody TransactionEditUpdateRequest request,
            Authentication authentication) {
        if (transNumber == null || transNumber <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Valid transaction number is required");
        }
        return transactionService.updateTransaction(
                transNumber,
                request,
                authentication.getName());
    }

    @GetMapping("/{transNumber}")
    public TransactionDetailResponse getTransaction(
            @PathVariable Long transNumber) {

        if (transNumber == null || transNumber <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Valid transaction number is required");
        }

        return transactionService.getTransactionByNumber(transNumber);
    }
}