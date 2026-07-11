package com.CasinoCtC.CCtCAPI.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CasinoCtC.CCtCAPI.dto.HeaderCardAssignRequest;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardAssignResponse;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardBatchLoadResponse;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardProcessResponse;
import com.CasinoCtC.CCtCAPI.service.HeaderCardService;

@RestController
@RequestMapping("/api/header-cards")
public class HeaderCardController {

    private final HeaderCardService headerCardService;

    public HeaderCardController(HeaderCardService headerCardService) {
        this.headerCardService = headerCardService;
    }

    @PostMapping("/assign")
    public HeaderCardAssignResponse saveAssignment(
            @RequestBody HeaderCardAssignRequest request,
            Principal principal) {
        return headerCardService.saveAssignment(request, principal != null ? principal.getName() : null);
    }

    @GetMapping("/assign/{headerCardId}")
    public HeaderCardAssignResponse getAssignment(@PathVariable String headerCardId) {
        return headerCardService.getAssignment(headerCardId);
    }

    @GetMapping("/assign")
    public List<HeaderCardAssignResponse> getAssignments() {
        return headerCardService.getAssignments();
    }

    @GetMapping("/process/{headerCardId}")
    public HeaderCardProcessResponse processLookup(@PathVariable String headerCardId) {
        return headerCardService.getProcessStatus(headerCardId);
    }

    @PostMapping("/load-batch")
    public HeaderCardBatchLoadResponse loadBatch(Principal principal) {
        return headerCardService.loadBatchHeaderCardFiles(principal != null ? principal.getName() : null);
    }
}
