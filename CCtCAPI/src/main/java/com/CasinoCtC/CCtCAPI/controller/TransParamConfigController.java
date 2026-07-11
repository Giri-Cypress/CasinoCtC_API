package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CasinoCtC.CCtCAPI.entity.TransParamConfigEntity;
import com.CasinoCtC.CCtCAPI.model.TransactionParamConfigResponse;
import com.CasinoCtC.CCtCAPI.service.TransParamConfigService;

@RestController
@RequestMapping("/api")
public class TransParamConfigController {

    private final TransParamConfigService service;

    public TransParamConfigController(TransParamConfigService service) {
        this.service = service;
    }

    @GetMapping("/transaction-params")
    public List<TransactionParamConfigResponse> getParameters() {
        return service.getActiveParameters();
    }
    
    @GetMapping("/transaction-params-admin")
    public List<TransParamConfigEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/transaction-params-admin/{paramNumber}")
    public TransParamConfigEntity getById(
            @PathVariable Integer paramNumber) {
        return service.getById(paramNumber);
    }

    @PostMapping("/transaction-params-admin")
    public TransParamConfigEntity save(
            @RequestBody TransParamConfigEntity model) {
        return service.save(model);
    }

    @DeleteMapping("/transaction-params-admin/{paramNumber}")
    public void delete(
            @PathVariable Integer paramNumber) {
        service.delete(paramNumber);
    }
}
