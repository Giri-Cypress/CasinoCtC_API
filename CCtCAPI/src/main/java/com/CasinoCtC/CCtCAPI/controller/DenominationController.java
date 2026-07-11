package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.CasinoCtC.CCtCAPI.dto.DenominationDto;
import com.CasinoCtC.CCtCAPI.service.DenominationService;

@RestController
@RequestMapping("/api/denominations")
public class DenominationController {
    private final DenominationService denominationService;

    public DenominationController(DenominationService denominationService) {
        this.denominationService = denominationService;
    }

    @GetMapping
    public List<DenominationDto> getActiveDenominations() {
        return denominationService.getActiveDenominations();
    }
}
