package com.CasinoCtC.CCtCAPI.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CasinoCtC.CCtCAPI.dto.CollectionPointResponse;
import com.CasinoCtC.CCtCAPI.dto.RecoveryTransactionResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionHeaderDetailResponse;
import com.CasinoCtC.CCtCAPI.entity.CollectionPointEntity;
import com.CasinoCtC.CCtCAPI.entity.TransactionHeaderEntity;
import com.CasinoCtC.CCtCAPI.entity.TransactionParamValueEntity;
import com.CasinoCtC.CCtCAPI.entity.TransactionParamValueId;
import com.CasinoCtC.CCtCAPI.entity.UserEntity;
import com.CasinoCtC.CCtCAPI.model.TransactionHeaderRequest;
import com.CasinoCtC.CCtCAPI.model.TransactionHeaderResponse;
import com.CasinoCtC.CCtCAPI.model.TransactionParameter;
import com.CasinoCtC.CCtCAPI.repository.CollectionPointRepository;
import com.CasinoCtC.CCtCAPI.repository.TransactionHeaderRepository;
import com.CasinoCtC.CCtCAPI.repository.TransactionParamValueRepository;
import com.CasinoCtC.CCtCAPI.repository.UserRepository;

@Service
public class TransactionHeaderService {

    private final TransactionHeaderRepository transactionHeaderRepository;
    private final TransactionParamValueRepository transactionParamValueRepository;
    private final ParameterValidationService parameterValidationService;
    private final CollectionPointRepository collectionPointRepository;
    private final UserRepository userRepository;

    public TransactionHeaderService(
            TransactionHeaderRepository transactionHeaderRepository,
            TransactionParamValueRepository transactionParamValueRepository,
            ParameterValidationService parameterValidationService,
            UserRepository userRepository,
            CollectionPointRepository collectionPointRepository) {

        this.transactionHeaderRepository =
                transactionHeaderRepository;

        this.transactionParamValueRepository =
                transactionParamValueRepository;

        this.parameterValidationService =
                parameterValidationService;

        this.userRepository =
                userRepository;

        this.collectionPointRepository =
                collectionPointRepository;
    }

    @Transactional(readOnly = true)
    public List<CollectionPointResponse> getCollectionPoints(
            String userName) {

        UserEntity user =
                userRepository
                        .findByUserName(userName)
                        .orElseThrow();

        List<CollectionPointEntity> points =
                collectionPointRepository
                        .findByLocationNumberAndStatusOrderByCollectionPointName(
                                user.getLocationNumber(),
                                1);

        return points.stream()
                .map(point -> {
                    CollectionPointResponse dto =
                            new CollectionPointResponse();

                    dto.setId(
                            point.getCollectionPointId());

                    dto.setName(
                            point.getCollectionPointName());

                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionHeaderDetailResponse getTransactionHeader(
            Integer transNumber) {

        TransactionHeaderEntity entity =
                transactionHeaderRepository
                        .findById(transNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"));

        TransactionHeaderDetailResponse response =
                new TransactionHeaderDetailResponse();

        response.setTransNumber(
                entity.getTransNumber());

        response.setBusinessDate(
                entity.getBusinessDate());

        response.setCollectionDate(
                entity.getCollectionDate());

        response.setBoxNumber(
                entity.getBoxNumber());

        response.setProcessUser(
                entity.getProcessUser());

        response.setStatus(
                entity.getStatus());

        return response;
    }

    @Transactional
    public void cancelHeader(
            Integer transNumber,
            String userName) {

        TransactionHeaderEntity header =
                transactionHeaderRepository
                        .findById(transNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction Header not found"));

        header.setStatus("CANCELLED");
        header.setCancelledBy(userName);
        header.setCancelTime(LocalDateTime.now());
        header.setLastUpdateBy(userName);
        header.setLastUpdateTime(LocalDateTime.now());

        transactionHeaderRepository.save(header);
    }

    @Transactional(readOnly = true)
    public RecoveryTransactionResponse findRecoveryTransaction(
            String processUser) {

        RecoveryTransactionResponse response =
                new RecoveryTransactionResponse();

        TransactionHeaderEntity header =
                transactionHeaderRepository
                        .findFirstByProcessUserAndStatusOrderByTransNumberDesc(
                                processUser,
                                "IN_PROGRESS")
                        .orElse(null);

        if (header == null) {
            response.setRecoverable(false);
            return response;
        }

        response.setRecoverable(true);

        response.setTransNumber(
                header.getTransNumber());

        response.setStatus(
                header.getStatus());

        return response;
    }

    @Transactional
    public long deleteRecoveryTransaction(
            String processUser) {

        return transactionHeaderRepository
                .deleteByProcessUserAndStatus(
                        processUser,
                        "IN_PROGRESS");
    }

    @Transactional
    public TransactionHeaderResponse createHeader(
            TransactionHeaderRequest request) {

        validateRequest(request);

        /*
         * Stored procedure / dynamic parameter validation remains here.
         * ParameterValidationService is the correct place to validate
         * parameters 3 and later because the validation procs are configured
         * in the database.
         */
        parameterValidationService.validate(
                request.getParameters());

        TransactionHeaderEntity existing =
                findExistingInProgressTransaction(
                        request);

        if (existing != null) {
            return new TransactionHeaderResponse(
                    existing.getTransNumber(),
                    existing.getStatus(),
                    true);
        }

        TransactionHeaderEntity entity =
                new TransactionHeaderEntity();

        entity.setBusinessDate(
                request.getBusinessDate());

        entity.setCollectionDate(
                request.getCollectionDate());

        entity.setBoxNumber(
                request.getBoxNumber());

        entity.setWorkstationId(
                request.getWorkstationId());

        entity.setProcessUser(
                request.getProcessUser());

        entity.setStatus("IN_PROGRESS");

        entity.setCreatedBy(
                request.getProcessUser());

        entity.setLastUpdateBy(
                request.getProcessUser());

        entity.setCreateTime(
                LocalDateTime.now());

        entity.setLastUpdateTime(
                LocalDateTime.now());

        entity =
                transactionHeaderRepository.save(entity);

        saveParameterValues(
                entity.getTransNumber(),
                request.getParameters());

        return new TransactionHeaderResponse(
                entity.getTransNumber(),
                entity.getStatus(),
                false);
    }

    private TransactionHeaderEntity findExistingInProgressTransaction(
            TransactionHeaderRequest request) {

        return transactionHeaderRepository
                .findFirstByBusinessDateAndCollectionDateAndBoxNumberAndStatus(
                        request.getBusinessDate(),
                        request.getCollectionDate(),
                        request.getBoxNumber(),
                        "IN_PROGRESS")
                .orElse(null);
    }

    private void saveParameterValues(
            Integer transNumber,
            List<TransactionParameter> parameters) {

        if (parameters == null) {
            return;
        }

        for (TransactionParameter parameter : parameters) {
            TransactionParamValueId id =
                    new TransactionParamValueId();

            id.setTransNumber(transNumber);

            if (parameter.getParamNumber() == null) {
                throw new RuntimeException(
                        "Parameter Number is required");
            }

            id.setParamNumber(
                    parameter.getParamNumber());

            TransactionParamValueEntity entity =
                    new TransactionParamValueEntity();

            entity.setId(id);

            entity.setParamValue(
                    parameter.getValue());

            transactionParamValueRepository.save(entity);
        }
    }

    private void validateRequest(
            TransactionHeaderRequest request) {

        if (request == null) {
            throw new RuntimeException(
                    "Transaction header request is required");
        }

        if (request.getBusinessDate() == null) {
            throw new RuntimeException(
                    "Business Date is required");
        }

        if (request.getCollectionDate() == null) {
            throw new RuntimeException(
                    "Collection Date is required");
        }

        if (request.getBoxNumber() == null
                || request.getBoxNumber().isBlank()) {

            throw new RuntimeException(
                    "Collection Container ID is required");
        }

        LocalDate businessDate =
                request.getBusinessDate();

        LocalDate collectionDate =
                request.getCollectionDate();

        LocalDate today =
                LocalDate.now();

        if (businessDate.isAfter(today)) {
            throw new RuntimeException(
                    "Business Date cannot be a future date");
        }

        if (collectionDate.isAfter(businessDate)) {
            throw new RuntimeException(
                    "Collection Date must be the Business Date or a prior date");
        }
    }
}
