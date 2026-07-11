package com.CasinoCtC.CCtCAPI.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.exception.BusinessValidationException;

@Service
public class ParameterProcedureValidationService {

    @PersistenceContext
    private EntityManager entityManager;

    public void validate(
            String procedureName,
            String parameterValue) {

        if (procedureName == null
                || procedureName.isBlank()) {
            return;
        }

        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery(
                        procedureName);

        query.registerStoredProcedureParameter(
                "param_value",
                String.class,
                ParameterMode.IN);

        query.setParameter(
                "param_value",
                parameterValue);

        try {

            query.execute();

        } catch (Exception ex) {

                String message = ex.getMessage();

                if (message != null) {

                    int start = message.indexOf('[');
                    int end = message.indexOf(']', start);

                    if (start >= 0 && end > start) {

                        String businessMessage =
                                message.substring(
                                        start + 1,
                                        end);

                        throw new BusinessValidationException(
                                businessMessage);
                    }
                }

                throw ex;
            }

       }
}