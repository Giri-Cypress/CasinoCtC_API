package com.CasinoCtC.CCtCAPI.service;

import java.util.List;


import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.entity.TransParamConfigEntity;
import com.CasinoCtC.CCtCAPI.repository.TransParamConfigRepository;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.CasinoCtC.CCtCAPI.entity.TransParamConfigEntity;
import com.CasinoCtC.CCtCAPI.model.TransactionParamConfigResponse;
import com.CasinoCtC.CCtCAPI.repository.TransParamConfigRepository;

@Service
public class TransParamConfigService {

    private final TransParamConfigRepository repository;

    public TransParamConfigService(TransParamConfigRepository repository) {
        this.repository = repository;
    }

    public List<TransactionParamConfigResponse> getActiveParameters() {
        return repository.findByParamInuseOrderByDisplayOrder(1)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TransactionParamConfigResponse toResponse(TransParamConfigEntity e) {
        TransactionParamConfigResponse r = new TransactionParamConfigResponse();
        r.setParamNumber(e.getParamNumber());
        r.setParamName(e.getParamName());
        r.setParamLabel(e.getParamLabel());
        r.setParamRequired(e.getParamRequired());
        r.setParamSize(e.getParamSize());
        r.setValidationProc(e.getValidationProc());
        return r;
    }
    public List<TransParamConfigEntity> getAll() {
        return repository.findAll(
            Sort.by("displayOrder")
        );
    }

    public TransParamConfigEntity getById(
            Integer paramNumber) {
        return repository.findById(paramNumber)
                .orElse(null);
    }

    public TransParamConfigEntity save(
            TransParamConfigEntity entity) {
        return repository.save(entity);
    }

    public void delete(
            Integer paramNumber) {
        repository.deleteById(paramNumber);
    }
}
