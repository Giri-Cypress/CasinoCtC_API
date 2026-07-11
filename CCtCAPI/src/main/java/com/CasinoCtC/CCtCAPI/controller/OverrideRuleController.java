package com.CasinoCtC.CCtCAPI.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CasinoCtC.CCtCAPI.dto.OverrideRuleResponse;
import com.CasinoCtC.CCtCAPI.dto.OverrideValidateRequest;
import com.CasinoCtC.CCtCAPI.dto.OverrideValidateResponse;
import com.CasinoCtC.CCtCAPI.service.OverrideRuleService;

@RestController
@RequestMapping("/api/override-rules")
public class OverrideRuleController {

    private final OverrideRuleService overrideRuleService;

    public OverrideRuleController(OverrideRuleService overrideRuleService) {
        this.overrideRuleService = overrideRuleService;
    }

    @GetMapping
    public List<OverrideRuleResponse> getAllRules() {
        return overrideRuleService.getAllRules();
    }

    @GetMapping("/{ruleNumber}")
    public OverrideRuleResponse getRule(@PathVariable Integer ruleNumber) {
        return overrideRuleService.getRule(ruleNumber);
    }

    @PostMapping("/validate")
    public OverrideValidateResponse validateOverride(
            @RequestBody OverrideValidateRequest request,
            Principal principal) {
        return overrideRuleService.validateOverride(
                request.getRuleNumbers(),
                request.getUserName(),
                request.getPassword(),
                request.getCashDifference(),
                request.getTicketDifference(),
                principal != null ? principal.getName() : null
        );
    }
}
