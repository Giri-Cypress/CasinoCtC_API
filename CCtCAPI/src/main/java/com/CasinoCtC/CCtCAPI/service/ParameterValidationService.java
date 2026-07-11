package com.CasinoCtC.CCtCAPI.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.entity.TransParamConfigEntity;
import com.CasinoCtC.CCtCAPI.exception.BusinessValidationException;
import com.CasinoCtC.CCtCAPI.model.TransactionParameter;
import com.CasinoCtC.CCtCAPI.repository.TransParamConfigRepository;

@Service
public class ParameterValidationService {

    private final TransParamConfigRepository repository;

    private final ParameterProcedureValidationService procedureValidationService;

    public ParameterValidationService(
            TransParamConfigRepository repository,
            ParameterProcedureValidationService procedureValidationService) {

        this.repository = repository;
        this.procedureValidationService = procedureValidationService;
    }

    public void validate(
            List<TransactionParameter> parameters) {

        List<TransParamConfigEntity> configs =
                repository.findByParamInuseOrderByDisplayOrder(1);

        Map<Integer, String> values =
                new HashMap<>();

        if (parameters != null) {
            for (TransactionParameter parameter : parameters) {

                if (parameter == null
                        || parameter.getParamNumber() == null) {
                    continue;
                }

                values.put(
                        parameter.getParamNumber(),
                        parameter.getValue());
            }
        }

        for (TransParamConfigEntity config : configs) {

            if (config == null
                    || config.getParamNumber() == null) {
                continue;
            }

            Integer paramNumber =
                    config.getParamNumber();

            String label =
                    getParameterLabel(config);

            String value =
                    values.get(paramNumber);

            /*
             * Required Validation
             */
            if (config.getParamRequired() != null
                    && config.getParamRequired() == 1) {

                if (value == null
                        || value.trim().isEmpty()) {

                    throw new BusinessValidationException(
                            label + " is required.");
                }
            }

            /*
             * Length Validation
             */
            if (value != null
                    && config.getParamSize() != null
                    && value.length() > config.getParamSize()) {

                throw new BusinessValidationException(
                        label
                                + " exceeds maximum length of "
                                + config.getParamSize()
                                + ".");
            }

            /*
             * Stored Procedure Validation
             *
             * Rule:
             * If validation_proc is populated for any active parameter,
             * execute the configured stored procedure.
             *
             * Example:
             * SHIFT is param_number 1 and uses usp_validate_shift.
             */
            if (value != null
                    && !value.trim().isEmpty()
                    && config.getValidationProc() != null
                    && !config.getValidationProc().isBlank()) {

                procedureValidationService.validate(
                        config.getValidationProc(),
                        value.trim());
            }
        }
    }

    private String getParameterLabel(
            TransParamConfigEntity config) {

        if (config.getParamLabel() != null
                && !config.getParamLabel().isBlank()) {
            return config.getParamLabel();
        }

        if (config.getParamName() != null
                && !config.getParamName().isBlank()) {
            return config.getParamName();
        }

        return "Parameter " + config.getParamNumber();
    }
}
