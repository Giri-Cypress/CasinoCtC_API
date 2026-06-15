package com.CasinoCtC.CCtCAPI.controller;

import com.CasinoCtC.CCtCAPI.dto.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import com.CasinoCtC.CCtCAPI.dto.TransactionSearchRequest;
import com.CasinoCtC.CCtCAPI.service.TransactionInquiryService;
import com.CasinoCtC.CCtCAPI.service.TransactionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor

public class TransactionController {

        @Autowired
      private TransactionService transactionService;

      /*
       * =========================================
       * NEW INQUIRY SERVICE (FOR VIEW DETAILS)
       * =========================================
       */
      @Autowired
      private TransactionInquiryService transactionInquiryService;


@GetMapping("/{id}")
public ResponseEntity<?> getTransactionById(
        @PathVariable Long id) {   // ✅ THIS FIXES 400

    System.out.println("🔥 CONTROLLER HIT: getTransactionById ID = " + id);

    return ResponseEntity.ok(
        transactionInquiryService.getTransactionById(id)
    );
}




    /**
     * =========================================
     * SAVE TRANSACTION
     * =========================================
     */
    @PostMapping
    public ResponseEntity<?> saveTransaction(
            @RequestBody
            TransactionRequest request,
            Authentication authentication
    ) {

        System.out.println("🔥 Controller hit");

        System.out.println(request);

        String username =
                authentication.getName();

        transactionService.saveTransaction(
                request,
                username
        );

        return ResponseEntity.ok().body(
                Map.of(
                        "message",
                        "Transaction Saved Successfully"
                )
        );
    }

    /**
     * =========================================
     * TRANSACTION INQUIRY SEARCH
     * =========================================
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchTransactions(
            @RequestBody
            TransactionSearchRequest request
    ) {

        System.out.println("🔍 Transaction Inquiry Search");

        System.out.println(request);

        return ResponseEntity.ok(
                transactionInquiryService
                        .searchTransactions(request)
        );
    }
}