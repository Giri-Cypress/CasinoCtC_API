package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CasinoCtC.CCtCAPI.dto.CollectionPointResponse;
import com.CasinoCtC.CCtCAPI.dto.RecoveryTransactionResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionHeaderDetailResponse;
import com.CasinoCtC.CCtCAPI.model.TransactionHeaderRequest;
import com.CasinoCtC.CCtCAPI.model.TransactionHeaderResponse;
import com.CasinoCtC.CCtCAPI.service.TransactionHeaderService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionHeaderController {

    private final TransactionHeaderService service;

    public TransactionHeaderController(
            TransactionHeaderService service) {
        this.service = service;
    }

    @PostMapping("/header")
    public TransactionHeaderResponse createHeader(
            @RequestBody TransactionHeaderRequest request) {
        return service.createHeader(request);
    }

    @GetMapping("/header/recovery/{processUser}")
    public RecoveryTransactionResponse getRecoveryTransaction(
            @PathVariable String processUser) {
        return service.findRecoveryTransaction(processUser);
    }

    @DeleteMapping("/header/recovery")
    public void deleteRecoveryTransaction(
            Authentication authentication) {

        service.deleteRecoveryTransaction(
                authentication.getName());
    }

    @GetMapping("/header/{transNumber}")
    public TransactionHeaderDetailResponse getTransactionHeader(
            @PathVariable Integer transNumber) {
        return service.getTransactionHeader(transNumber);
    }

    @GetMapping("/collection-points")
    public List<CollectionPointResponse> getCollectionPoints(
            Authentication authentication) {
        return service.getCollectionPoints(
                authentication.getName());
    }
}