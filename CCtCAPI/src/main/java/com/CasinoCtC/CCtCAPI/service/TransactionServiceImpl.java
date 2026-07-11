package com.CasinoCtC.CCtCAPI.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.CasinoCtC.CCtCAPI.dto.PendingTransactionResponse;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardCountsResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionCurrencyDto;
import com.CasinoCtC.CCtCAPI.dto.TransactionDetailResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionEditDetailResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionEditParamDto;
import com.CasinoCtC.CCtCAPI.dto.TransactionEditUpdateRequest;
import com.CasinoCtC.CCtCAPI.dto.TransactionHeaderDto;
import com.CasinoCtC.CCtCAPI.dto.TransactionInquiryRowResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionSaveRequest;
import com.CasinoCtC.CCtCAPI.dto.TransactionSaveResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionSearchRequest;
import com.CasinoCtC.CCtCAPI.dto.TransactionTicketDto;
import com.CasinoCtC.CCtCAPI.entity.TransactionCurrencyEntity;
import com.CasinoCtC.CCtCAPI.entity.TransactionCurrencyId;
import com.CasinoCtC.CCtCAPI.entity.TransactionEntity;
import com.CasinoCtC.CCtCAPI.entity.TransactionTicketEntity;
import com.CasinoCtC.CCtCAPI.entity.TransactionTicketId;
import com.CasinoCtC.CCtCAPI.entity.UserEntity;
import com.CasinoCtC.CCtCAPI.repository.DenominationRepository;
import com.CasinoCtC.CCtCAPI.repository.TransactionCurrencyRepository;
import com.CasinoCtC.CCtCAPI.repository.TransactionHeaderRepository;
import com.CasinoCtC.CCtCAPI.repository.TransactionRepository;
import com.CasinoCtC.CCtCAPI.repository.TransactionTicketRepository;
import com.CasinoCtC.CCtCAPI.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionCurrencyRepository transactionCurrencyRepository;
    private final TransactionTicketRepository transactionTicketRepository;
    private final UserRepository userRepository;
    private final TransactionHeaderRepository transactionHeaderRepository;
    private final DenominationRepository denominationRepository;
    private final HeaderCardCountsService headerCardCountsService;
    private final SupervisorActivityService supervisorActivityService;

    @PersistenceContext
    private EntityManager entityManager;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            TransactionCurrencyRepository transactionCurrencyRepository,
            TransactionTicketRepository transactionTicketRepository,
            UserRepository userRepository,
            TransactionHeaderRepository transactionHeaderRepository,
            DenominationRepository denominationRepository,
            HeaderCardCountsService headerCardCountsService,
            SupervisorActivityService supervisorActivityService) {

        this.transactionRepository = transactionRepository;
        this.transactionCurrencyRepository = transactionCurrencyRepository;
        this.transactionTicketRepository = transactionTicketRepository;
        this.userRepository = userRepository;
        this.transactionHeaderRepository = transactionHeaderRepository;
        this.denominationRepository = denominationRepository;
        this.headerCardCountsService = headerCardCountsService;
        this.supervisorActivityService = supervisorActivityService;
    }

    @Override
    @Transactional(readOnly = true)
    public PendingTransactionResponse getPendingTransaction(String userName) {

        UserEntity user =
                userRepository.findByUserName(userName)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found"));

        TransactionEntity transaction =
                transactionRepository
                        .findFirstByUserNumberAndStatusOrderByCreatedAtDesc(
                                user.getUserNumber(),
                                "DRAFT")
                        .orElse(null);

        if (transaction == null) {
            return null;
        }

        PendingTransactionResponse response =
                new PendingTransactionResponse();

        response.setTransNumber(transaction.getTransNumber());
        response.setBusinessDate(transaction.getBusinessDate().toString());
        response.setCollectionDate(transaction.getCollectionDate().toString());
        response.setBoxNumber(transaction.getBoxNumber());
        response.setStatus(transaction.getStatus());

        return response;
    }

    @Override
    @Transactional
    public void cancelTransaction(Long transNumber) {

        TransactionEntity transaction =
                transactionRepository.findById(transNumber)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Transaction not found"));

        transaction.setStatus("CANCEL");
        transaction.setUpdatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public TransactionSaveResponse saveTransaction(
            TransactionSaveRequest request,
            String userName) {

        if (request == null || request.getHeader() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction header is required");
        }

        UserEntity user =
                userRepository.findByUserName(userName)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Authenticated user not found"));

        long totalCurrency =
                calculateTotalCurrency(request.getCash());

        long totalTicketAmount =
                calculateTotalTickets(request.getTickets());

        int ticketCount =
                request.getTickets() == null
                        ? 0
                        : request.getTickets().size();

        long totalAmount =
                totalCurrency + totalTicketAmount;

        long declaredCurrencyTotal =
                toCentsLong(requestValue(request, "declaredCashTotal"));

        Integer declaredTicketCount =
                request.getDeclaredTicketCount() == null
                        ? 0
                        : request.getDeclaredTicketCount();

        Integer supervisorOverride =
                request.getSupervisorOverride();

        String requestedStatus =
                resolveRequestedStatus(requestValue(request, "status"));

        boolean headerCardEnabled =
                isUseHeaderCardEnabledForLocation(user.getLocationNumber());

        String normalizedHeaderCardForSave =
                headerCardEnabled
                        ? normalizeHeaderCard(request.getHeaderCard())
                        : null;

        String headerCardFileNameForSave =
                headerCardEnabled
                        && normalizedHeaderCardForSave != null
                        && !isBlank(request.getHeaderCardFileName())
                                ? safeTrim(request.getHeaderCardFileName())
                                : null;

        TransactionEntity header =
                new TransactionEntity();

        header.setBusinessDate(
                parseDate(request.getHeader().getBusinessDate()));

        header.setCollectionDate(
                parseDate(request.getHeader().getCollectionDate()));

        header.setBoxNumber(
                request.getHeader().getBoxNumber());

        header.setLocationNumber(
                user.getLocationNumber());

        header.setUserNumber(
                user.getUserNumber());

        header.setStatus(
                requestedStatus);

        header.setDeclaredCurrencyTotal(
                declaredCurrencyTotal);

        header.setDeclaredTicketCount(
                declaredTicketCount);

        header.setSupervisorOverride(
                supervisorOverride);

        header.setTotalCurrency(
                totalCurrency);

        header.setTicketCount(
                ticketCount);

        header.setTotalTktAmount(
                totalTicketAmount);

        header.setTotalAmount(
                totalAmount);

        header.setCreatedAt(
                LocalDateTime.now());

        header.setUpdatedAt(
                LocalDateTime.now());

        header.setTransNumberEdited(null);

        header.setHeaderCard(normalizedHeaderCardForSave);
        header.setHeaderCardFile(headerCardFileNameForSave);

        TransactionEntity savedHeader =
                transactionRepository.save(header);

        supervisorActivityService.attachToTransaction(
                request.getSupervisorActivityNumbers(),
                savedHeader.getTransNumber());

        saveCurrencyRows(
                savedHeader.getTransNumber(),
                request.getCash());

        saveTicketRows(
                savedHeader.getTransNumber(),
                request.getTickets());

        copyParameterRowsFromProcessHeader(
                savedHeader.getTransNumber(),
                userName);

        deleteParameterRowsForProcessHeader(userName);

        updateInventoryForProcessedTransaction(
                savedHeader,
                request.getCash());

        if (!isBlank(headerCardFileNameForSave)) {
            headerCardCountsService.moveProcessedFileToSaveFolder(userName, headerCardFileNameForSave);
        }

        if ("PROCESSED".equalsIgnoreCase(requestedStatus)
                && normalizedHeaderCardForSave != null) {
            markHeaderCardAssignmentSubmitted(normalizedHeaderCardForSave);
        }

        TransactionSaveResponse response =
                buildSaveResponse(savedHeader);

        deleteTransactionHeadersForProcessUser(userName);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDetailResponse getTransactionByNumber(
            Long transNumber) {

        TransactionEntity header =
                transactionRepository.findById(transNumber)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Transaction not found"));

        TransactionDetailResponse response =
                new TransactionDetailResponse();

        response.setTransNumber(
                header.getTransNumber());

        response.setUpdatedAt(
                header.getUpdatedAt() != null
                        ? header.getUpdatedAt().toString()
                        : null);

        response.setDeclaredCashTotal(
                header.getDeclaredCurrencyTotal());

        response.setDeclaredTicketCount(
                header.getDeclaredTicketCount());

        response.setHeaderCard(
                header.getHeaderCard());

        response.setHeaderCardFile(
                header.getHeaderCardFile());

        Long declaredCashTotal =
                header.getDeclaredCurrencyTotal() != null
                        ? header.getDeclaredCurrencyTotal()
                        : 0L;

        Long actualCurrencyTotal =
                header.getTotalCurrency() != null
                        ? header.getTotalCurrency()
                        : 0L;

        response.setCashDifference(
                declaredCashTotal - actualCurrencyTotal);

        if (header.getSupervisorOverride() != null) {
            response.setSupervisorOverrideUserNumber(
                    header.getSupervisorOverride());

            userRepository.findById(header.getSupervisorOverride())
                    .map(UserEntity::getUserName)
                    .ifPresent(response::setSupervisorName);
        }


        if (header.getUserNumber() != null) {
            userRepository.findById(header.getUserNumber())
                    .map(UserEntity::getUserName)
                    .ifPresent(response::setUserName);
        }

        TransactionHeaderDto headerDto =
                new TransactionHeaderDto();

        headerDto.setBusinessDate(
                header.getBusinessDate() != null
                        ? header.getBusinessDate().toString()
                        : null);

        headerDto.setCollectionDate(
                header.getCollectionDate() != null
                        ? header.getCollectionDate().toString()
                        : null);

        headerDto.setBoxNumber(
                header.getBoxNumber());

        response.setHeader(headerDto);

        List<TransactionCurrencyDto> cash =
                new ArrayList<>();

        for (TransactionCurrencyEntity row :
                transactionCurrencyRepository
                        .findByIdTransNumberOrderByIdDenomNumberAsc(
                                transNumber)) {

            TransactionCurrencyDto dto =
                    new TransactionCurrencyDto();

            dto.setDenomination(
                    row.getDenomNumber());

            dto.setCount(
                    row.getCount() == null
                            ? 0
                            : row.getCount());

            dto.setMachineCount(
                    row.getCountMachine() == null
                            ? 0
                            : row.getCountMachine());

            dto.setAmount(
                    toCentsLong(requestValue(row, "amount")));

            cash.add(dto);
        }

        response.setCash(cash);

        List<TransactionTicketDto> tickets =
                new ArrayList<>();

        for (TransactionTicketEntity row :
                transactionTicketRepository
                        .findByIdTransNumberOrderByIdTicketIdAsc(
                                transNumber)) {

            TransactionTicketDto dto =
                    new TransactionTicketDto();

            dto.setNumber(
                    row.getTicketId());

            dto.setAmount(
                    toCentsLong(requestValue(row, "amount")));

            tickets.add(dto);
        }

        response.setTickets(tickets);

        response.setSummary(
                buildSaveResponse(header));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionInquiryRowResponse> searchTransactions(
            TransactionSearchRequest request) {

        StringBuilder sql =
                new StringBuilder("""
                    SELECT
                        t.trans_number,
                        t.business_date,
                        t.collection_date,
                        t.box_number,
                        t.header_card,
                        t.status,
                        t.user_number,
                        t.total_currency,
                        t.ticket_count,
                        t.total_tkt_amount,
                        t.total_amount
                    FROM gsi.transactions t
                    WHERE 1 = 1
                """);

        Map<String, Object> params =
                new LinkedHashMap<>();

        Object transNumber =
                requestValue(request, "transNumber");

        if (transNumber != null && !isBlank(transNumber)) {
            sql.append(" AND t.trans_number = :transNumber");
            params.put("transNumber", toLong(transNumber));
        }

        Object status =
                requestValue(request, "status");

        if (status != null && !isBlank(status)) {
            sql.append(" AND t.status = :status");
            params.put("status", String.valueOf(status));
        }

        Object boxNumber =
                requestValue(request, "boxNumber");

        if (boxNumber != null && !isBlank(boxNumber)) {
            sql.append(" AND t.box_number = :boxNumber");
            params.put("boxNumber", String.valueOf(boxNumber));
        }

        Object boxNumberFrom =
                requestValue(request, "boxNumberFrom");

        if (boxNumberFrom != null && !isBlank(boxNumberFrom)) {
            sql.append(" AND t.box_number >= :boxNumberFrom");
            params.put("boxNumberFrom", String.valueOf(boxNumberFrom));
        }

        Object boxNumberTo =
                requestValue(request, "boxNumberTo");

        if (boxNumberTo != null && !isBlank(boxNumberTo)) {
            sql.append(" AND t.box_number <= :boxNumberTo");
            params.put("boxNumberTo", String.valueOf(boxNumberTo));
        }

        Object employeeNumber =
                requestValue(request, "employeeNumber");

        if (employeeNumber != null && !isBlank(employeeNumber)) {
            sql.append(" AND t.user_number = :employeeNumber");
            params.put("employeeNumber", toLong(employeeNumber));
        }

        addDateFilter(
                sql,
                params,
                "businessDateFrom",
                " AND t.business_date >= :businessDateFrom",
                request,
                "businessDateFrom",
                "fromBusinessDate",
                "startBusinessDate");

        addDateFilter(
                sql,
                params,
                "businessDateTo",
                " AND t.business_date <= :businessDateTo",
                request,
                "businessDateTo",
                "toBusinessDate",
                "endBusinessDate");

        addDateFilter(
                sql,
                params,
                "collectionDateFrom",
                " AND t.collection_date >= :collectionDateFrom",
                request,
                "collectionDateFrom",
                "fromCollectionDate",
                "startCollectionDate");

        addDateFilter(
                sql,
                params,
                "collectionDateTo",
                " AND t.collection_date <= :collectionDateTo",
                request,
                "collectionDateTo",
                "toCollectionDate",
                "endCollectionDate");

        sql.append(" ORDER BY t.trans_number DESC");

        Query query =
                entityManager.createNativeQuery(
                        sql.toString());

        params.forEach(query::setParameter);

        @SuppressWarnings("unchecked")
        List<Object[]> rows =
                query.getResultList();

        List<TransactionInquiryRowResponse> results =
                new ArrayList<>();

        for (Object[] row : rows) {
            TransactionInquiryRowResponse dto =
                    new TransactionInquiryRowResponse();

            setProperty(dto, "transactionNumber", row[0]);
            setProperty(dto, "businessDate", row[1]);
            setProperty(dto, "collectionDate", row[2]);
            setProperty(dto, "boxNumber", row[3]);
            setProperty(dto, "headerCard", row[4]);
            setProperty(dto, "status", row[5]);
            setProperty(dto, "employeeNumber", row[6]);
            setProperty(dto, "currencyTotal", row[7]);
            setProperty(dto, "ticketTotal", row[9]);
            setProperty(dto, "grandTotal", row[10]);

            results.add(dto);
        }

        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionEditDetailResponse getTransactionForEdit(
            Long transNumber) {

        TransactionEntity header =
                transactionRepository.findById(transNumber)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Transaction not found"));

        TransactionEditDetailResponse response =
                new TransactionEditDetailResponse();

        response.setTransNumber(
                header.getTransNumber());

        response.setLocationNumber(
                header.getLocationNumber());

        response.setUserNumber(
                header.getUserNumber());

        response.setStatus(
                header.getStatus());

        response.setHeaderCard(
                header.getHeaderCard());

        if (header.getUserNumber() != null) {
            userRepository.findById(header.getUserNumber())
                    .map(UserEntity::getUserName)
                    .ifPresent(response::setUserName);
        }

        TransactionHeaderDto headerDto =
                new TransactionHeaderDto();

        headerDto.setBusinessDate(
                header.getBusinessDate() != null
                        ? header.getBusinessDate().toString()
                        : null);

        headerDto.setCollectionDate(
                header.getCollectionDate() != null
                        ? header.getCollectionDate().toString()
                        : null);

        headerDto.setBoxNumber(
                header.getBoxNumber());

        response.setHeader(headerDto);

        response.setCash(
                loadCurrencyRowsForEdit(transNumber));

        response.setTickets(
                loadTicketRowsForEdit(transNumber));

        response.setParameters(
                loadParameterRowsForEdit(transNumber));

        response.setSummary(
                buildSaveResponse(header));

        return response;
    }

    @Override
    @Transactional
    public TransactionSaveResponse updateTransaction(
            Long transNumber,
            TransactionEditUpdateRequest request,
            String userName) {

        if (request == null || request.getHeader() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction header is required");
        }

        TransactionEntity oldTransaction =
                transactionRepository.findById(transNumber)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Transaction not found"));

        long totalCurrency =
                calculateTotalCurrency(request.getCash());

        long totalTicketAmount =
                calculateTotalTickets(request.getTickets());

        int ticketCount =
                request.getTickets() == null
                        ? 0
                        : request.getTickets().size();

        long totalAmount =
                totalCurrency + totalTicketAmount;

        long declaredCurrencyTotal =
                request.getDeclaredCashTotal() == null
                        ? totalCurrency
                        : toCentsLong(request.getDeclaredCashTotal());

        Integer declaredTicketCount =
                request.getDeclaredTicketCount() == null
                        ? ticketCount
                        : request.getDeclaredTicketCount();

        /*
         * IMPORTANT:
         * Do not overwrite the old transaction anymore.
         * Mark old transaction as EDITED and preserve its original details.
         */
        oldTransaction.setStatus("EDITED");
        oldTransaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(oldTransaction);

        /*
         * Create a brand-new replacement transaction.
         * The new transaction points back to the old transaction using
         * gsi.transactions.trans_number_edited.
         */
        TransactionEntity newTransaction =
                new TransactionEntity();

        newTransaction.setBusinessDate(
                parseDate(request.getHeader().getBusinessDate()));

        newTransaction.setCollectionDate(
                parseDate(request.getHeader().getCollectionDate()));

        newTransaction.setBoxNumber(
                request.getHeader().getBoxNumber());

        newTransaction.setLocationNumber(
                oldTransaction.getLocationNumber());

        newTransaction.setUserNumber(
                oldTransaction.getUserNumber());

        newTransaction.setStatus(
                resolveRequestedStatus(request.getStatus()));

        newTransaction.setDeclaredCurrencyTotal(
                declaredCurrencyTotal);

        newTransaction.setDeclaredTicketCount(
                declaredTicketCount);

        newTransaction.setSupervisorOverride(
                request.getSupervisorOverride());

        newTransaction.setTotalCurrency(
                totalCurrency);

        newTransaction.setTicketCount(
                ticketCount);

        newTransaction.setTotalTktAmount(
                totalTicketAmount);

        newTransaction.setTotalAmount(
                totalAmount);

        newTransaction.setCreatedAt(
                LocalDateTime.now());

        newTransaction.setUpdatedAt(
                LocalDateTime.now());

        newTransaction.setTransNumberEdited(
                transNumber);

        newTransaction.setHeaderCard(
                oldTransaction.getHeaderCard());

        TransactionEntity savedNewTransaction =
                transactionRepository.save(newTransaction);

        saveCurrencyRows(
                savedNewTransaction.getTransNumber(),
                request.getCash());

        saveTicketRows(
                savedNewTransaction.getTransNumber(),
                request.getTickets());

        saveParameterRowsForEdit(
                savedNewTransaction.getTransNumber(),
                request.getParameters());

        return buildSaveResponse(savedNewTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUseHeaderCardEnabled(String userName) {
        UserEntity user =
                userRepository.findByUserName(userName)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Authenticated user not found"));

        return isConfigEnabledForLocation(user.getLocationNumber(), "Use Header Card");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getHeaderCardSettings(String userName) {
        UserEntity user =
                userRepository.findByUserName(userName)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Authenticated user not found"));

        Map<String, Object> response = new LinkedHashMap<>();
        ConfigLookupResult useHeaderCard = getConfigForLocation(user.getLocationNumber(), "Use Header Card");
        ConfigLookupResult useAutoHeaderCard = getConfigForLocation(user.getLocationNumber(), "Use Auto Header Card");
        ConfigLookupResult useBatchHeaderCards = getConfigForLocation(user.getLocationNumber(), "Use Batch Header Cards");

        response.put("locationNumber", user.getLocationNumber());
        response.put("useHeaderCard", useHeaderCard.enabled);
        response.put("useHeaderCardValue", useHeaderCard.value);
        response.put("useHeaderCardSource", useHeaderCard.source);
        response.put("useAutoHeaderCard", useAutoHeaderCard.enabled);
        response.put("useAutoHeaderCardValue", useAutoHeaderCard.value);
        response.put("useAutoHeaderCardSource", useAutoHeaderCard.source);
        response.put("useBatchHeaderCards", useBatchHeaderCards.enabled);
        response.put("useBatchHeaderCardsValue", useBatchHeaderCards.value);
        response.put("useBatchHeaderCardsSource", useBatchHeaderCards.source);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public HeaderCardCountsResponse getHeaderCardCounts(String userName, String headerCard) {
        return headerCardCountsService.getCounts(userName, headerCard);
    }

    @Override
    @Transactional(readOnly = true)
    public HeaderCardCountsResponse getDeviceTicketFtpCounts(String userName, String ticketFtpId) {
        if (isBlank(ticketFtpId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket FTP ID is required for the selected device.");
        }
        return headerCardCountsService.getCountsByFilePrefix(userName, ticketFtpId.trim());
    }

    private boolean isUseHeaderCardEnabledForLocation(Integer locationNumber) {
        return isConfigEnabledForLocation(locationNumber, "Use Header Card");
    }

    private boolean isConfigEnabledForLocation(Integer locationNumber, String label) {
        return getConfigForLocation(locationNumber, label).enabled;
    }

    private ConfigLookupResult getConfigForLocation(Integer locationNumber, String label) {
        if (locationNumber == null || label == null || label.trim().isEmpty()) {
            return new ConfigLookupResult(false, "default", null);
        }

        String normalizedLabel = normalizeConfigLabel(label);

        String localSql = """
            SELECT TOP 1 lc_value
              FROM gsi.local_config
             WHERE lc_location = :locationNumber
               AND LOWER(REPLACE(REPLACE(LTRIM(RTRIM(lc_label)), ' ', ''), '_', '')) = :normalizedLabel
               AND COALESCE(lc_status, 1) = 1
             ORDER BY lc_number DESC
            """;

        Query localQuery = entityManager.createNativeQuery(localSql);
        localQuery.setParameter("locationNumber", locationNumber);
        localQuery.setParameter("normalizedLabel", normalizedLabel);
        List<?> localResult = localQuery.getResultList();
        if (!localResult.isEmpty()) {
            Object value = localResult.get(0);
            return new ConfigLookupResult(isEnabledConfigValue(value), "local", value == null ? null : String.valueOf(value));
        }

        String globalSql = """
            SELECT TOP 1 gc_value
              FROM gsi.global_config
             WHERE LOWER(REPLACE(REPLACE(LTRIM(RTRIM(gc_label)), ' ', ''), '_', '')) = :normalizedLabel
               AND COALESCE(gc_status, 1) = 1
             ORDER BY gc_number DESC
            """;

        Query globalQuery = entityManager.createNativeQuery(globalSql);
        globalQuery.setParameter("normalizedLabel", normalizedLabel);
        List<?> globalResult = globalQuery.getResultList();
        if (globalResult.isEmpty()) {
            return new ConfigLookupResult(false, "default", null);
        }

        Object value = globalResult.get(0);
        return new ConfigLookupResult(isEnabledConfigValue(value), "global", value == null ? null : String.valueOf(value));
    }

    private String normalizeConfigLabel(String label) {
        return label == null ? "" : label.trim().replace(" ", "").replace("_", "").toLowerCase();
    }

    private static class ConfigLookupResult {
        private final boolean enabled;
        private final String source;
        private final String value;

        private ConfigLookupResult(boolean enabled, String source, String value) {
            this.enabled = enabled;
            this.source = source;
            this.value = value;
        }
    }

    private boolean isEnabledConfigValue(Object value) {
        if (value == null) {
            return false;
        }
        String normalizedValue = String.valueOf(value).trim();
        return "yes".equalsIgnoreCase(normalizedValue)
                || "y".equalsIgnoreCase(normalizedValue)
                || "true".equalsIgnoreCase(normalizedValue)
                || "1".equals(normalizedValue);
    }

    private String normalizeHeaderCard(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.length() > 64 ? trimmed.substring(0, 64) : trimmed;
    }

    private void updateInventoryForProcessedTransaction(
            TransactionEntity savedHeader,
            List<TransactionCurrencyDto> cashRows) {

        if (savedHeader == null
                || !"PROCESSED".equalsIgnoreCase(savedHeader.getStatus())) {
            return;
        }

        Integer locationNumber =
                savedHeader.getLocationNumber();

        Integer userNumber =
                savedHeader.getUserNumber();

        if (locationNumber == null || userNumber == null) {
            return;
        }

        LocalDateTime now =
                LocalDateTime.now();

        long newCashAmount =
                toCentsLong(requestValue(savedHeader, "totalCurrency"));

        int inventoryRowsUpdated =
                entityManager.createNativeQuery("""
                    UPDATE gsi.inventory
                       SET cash_amt = cash_amt + :cashAmt,
                           updated_at = :updatedAt
                     WHERE location_number = :locationNumber
                       AND user_number = :userNumber
                """)
                        .setParameter("cashAmt", newCashAmount)
                        .setParameter("updatedAt", now)
                        .setParameter("locationNumber", locationNumber)
                        .setParameter("userNumber", userNumber)
                        .executeUpdate();

        if (inventoryRowsUpdated == 0) {
            entityManager.createNativeQuery("""
                INSERT INTO gsi.inventory
                    (location_number, user_number, cash_amt, updated_at)
                VALUES
                    (:locationNumber, :userNumber, :cashAmt, :updatedAt)
            """)
                    .setParameter("locationNumber", locationNumber)
                    .setParameter("userNumber", userNumber)
                    .setParameter("cashAmt", newCashAmount)
                    .setParameter("updatedAt", now)
                    .executeUpdate();
        }

        if (cashRows == null || cashRows.isEmpty()) {
            return;
        }

        Map<Integer, Long> amountByDenomination =
                new LinkedHashMap<>();

        for (TransactionCurrencyDto row : cashRows) {
            if (row == null || row.getDenomination() == null) {
                continue;
            }

            long amount =
                    toCentsLong(requestValue(row, "amount"));

            if (amount <= 0L) {
                continue;
            }

            amountByDenomination.merge(
                    row.getDenomination(),
                    amount,
                    Long::sum);
        }

        for (Map.Entry<Integer, Long> entry :
                amountByDenomination.entrySet()) {

            Integer denomNumber =
                    entry.getKey();

            Long amount =
                    entry.getValue();

            validateDenominationExists(denomNumber);

            int detailRowsUpdated =
                    entityManager.createNativeQuery("""
                        UPDATE gsi.inventory_dtl
                           SET amount = amount + :amount,
                               updated_at = :updatedAt
                         WHERE location_number = :locationNumber
                           AND user_number = :userNumber
                           AND denom_number = :denomNumber
                    """)
                            .setParameter("amount", amount)
                            .setParameter("updatedAt", now)
                            .setParameter("locationNumber", locationNumber)
                            .setParameter("userNumber", userNumber)
                            .setParameter("denomNumber", denomNumber)
                            .executeUpdate();

            if (detailRowsUpdated == 0) {
                entityManager.createNativeQuery("""
                    INSERT INTO gsi.inventory_dtl
                        (location_number, user_number, denom_number, amount, updated_at)
                    VALUES
                        (:locationNumber, :userNumber, :denomNumber, :amount, :updatedAt)
                """)
                        .setParameter("locationNumber", locationNumber)
                        .setParameter("userNumber", userNumber)
                        .setParameter("denomNumber", denomNumber)
                        .setParameter("amount", amount)
                        .setParameter("updatedAt", now)
                        .executeUpdate();
            }
        }
    }

    private void validateDenominationExists(
            Integer denomNumber) {

        if (denomNumber == null
                || !denominationRepository.existsById(denomNumber)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid denomination number: "
                            + denomNumber
                            + ". Use denomination.denom_number from /api/denominations.");
        }
    }



    private void copyParameterRowsFromProcessHeader(
            Long finalTransNumber,
            String processUser) {

        if (finalTransNumber == null
                || processUser == null
                || processUser.isBlank()) {
            return;
        }

        entityManager.createNativeQuery("""
            INSERT INTO gsi.transaction_param_values
                (trans_number, param_number, param_value)
            SELECT :finalTransNumber, v.param_number, v.param_value
              FROM gsi.transaction_param_values v
              INNER JOIN (
                    SELECT TOP 1 h.trans_number
                      FROM gsi.transaction_header h
                     WHERE h.process_user = :processUser
                       AND h.status = 'IN_PROGRESS'
                     ORDER BY h.trans_number DESC
              ) h ON h.trans_number = v.trans_number
             WHERE NOT EXISTS (
                    SELECT 1
                      FROM gsi.transaction_param_values existingRow
                     WHERE existingRow.trans_number = :finalTransNumber
                       AND existingRow.param_number = v.param_number
              )
        """)
                .setParameter("finalTransNumber", finalTransNumber)
                .setParameter("processUser", processUser)
                .executeUpdate();
    }

    private void deleteParameterRowsForProcessHeader(
            String processUser) {

        if (processUser == null || processUser.isBlank()) {
            return;
        }

        entityManager.createNativeQuery("""
            DELETE v
              FROM gsi.transaction_param_values v
              INNER JOIN gsi.transaction_header h
                      ON h.trans_number = v.trans_number
             WHERE h.process_user = :processUser
               AND h.status = 'IN_PROGRESS'
        """)
                .setParameter("processUser", processUser)
                .executeUpdate();
    }

    private void deleteTransactionHeadersForProcessUser(
            String processUser) {

        if (processUser == null || processUser.isBlank()) {
            return;
        }

        transactionHeaderRepository.deleteByProcessUser(processUser);
    }

    private void saveCurrencyRows(
            Long transNumber,
            List<TransactionCurrencyDto> cashRows) {

        if (cashRows == null) {
            return;
        }

        List<TransactionCurrencyEntity> rows =
                new ArrayList<>();

        for (TransactionCurrencyDto row : cashRows) {
            if (row == null || row.getDenomination() == null) {
                continue;
            }

            validateDenominationExists(row.getDenomination());

            int manualCount =
                    row.getCount() == null
                            ? 0
                            : row.getCount();

            int machineCount =
                    row.getMachineCount() == null
                            ? 0
                            : row.getMachineCount();

            int combinedCount =
                    manualCount + machineCount;

            long amount =
                    toCentsLong(requestValue(row, "amount"));

            if (combinedCount <= 0 && amount <= 0L) {
                continue;
            }

            TransactionCurrencyEntity entity =
                    new TransactionCurrencyEntity();

            entity.setId(
                    new TransactionCurrencyId(
                            transNumber,
                            row.getDenomination(),
                            0,
                            machineCount > 0 ? "MIXED" : "MANUAL"));

            entity.setCount(manualCount);
            entity.setCountMachine(machineCount);

            setProperty(entity, "amount", amount);

            rows.add(entity);
        }

        transactionCurrencyRepository.saveAll(rows);
    }

    private void saveTicketRows(
            Long transNumber,
            List<TransactionTicketDto> ticketRows) {

        if (ticketRows == null) {
            return;
        }

        List<TransactionTicketEntity> rows =
                new ArrayList<>();

        for (TransactionTicketDto row : ticketRows) {
            if (row.getNumber() == null
                    || row.getNumber().isBlank()) {
                continue;
            }

            TransactionTicketEntity entity =
                    new TransactionTicketEntity();

            entity.setId(
                    new TransactionTicketId(
                            transNumber,
                            row.getNumber()));

            setProperty(
                    entity,
                    "amount",
                    toCentsLong(requestValue(row, "amount")));

            rows.add(entity);
        }

        transactionTicketRepository.saveAll(rows);
    }

    private long calculateTotalCurrency(
            List<TransactionCurrencyDto> cashRows) {

        if (cashRows == null) {
            return 0L;
        }

        long total = 0L;

        for (TransactionCurrencyDto row : cashRows) {
            total += toCentsLong(
                    requestValue(row, "amount"));
        }

        return total;
    }

    private long calculateTotalTickets(
            List<TransactionTicketDto> ticketRows) {

        if (ticketRows == null) {
            return 0L;
        }

        long total = 0L;

        for (TransactionTicketDto row : ticketRows) {
            total += toCentsLong(
                    requestValue(row, "amount"));
        }

        return total;
    }

    private void markHeaderCardAssignmentSubmitted(String headerCardId) {
        if (isBlank(headerCardId)) {
            return;
        }

        Query query = entityManager.createNativeQuery(
                "UPDATE gsi.header_card_assign "
                        + "SET status = 2 "
                        + "WHERE hc_assign_number = ("
                        + "SELECT TOP 1 hc_assign_number "
                        + "FROM gsi.header_card_assign "
                        + "WHERE header_card_ID = :headerCardId "
                        + "AND status = 1 "
                        + "ORDER BY hc_assign_number DESC"
                        + ")");
        query.setParameter("headerCardId", headerCardId);
        query.executeUpdate();
    }

    private String resolveRequestedStatus(
            Object rawStatus) {

        if (rawStatus == null
                || String.valueOf(rawStatus).isBlank()) {
            return "PROCESSED";
        }

        String normalized =
                String.valueOf(rawStatus)
                        .trim()
                        .toUpperCase();

        if ("CANCEL".equals(normalized)
                || "CANCELLED".equals(normalized)) {
            return "CANCEL";
        }

        if ("EDITED".equals(normalized)) {
            return "PROCESSED";
        }

        return "PROCESSED";
    }

    private void addDateFilter(
            StringBuilder sql,
            Map<String, Object> params,
            String paramName,
            String clause,
            Object request,
            String... requestPropertyNames) {

        Object value = null;

        for (String propertyName : requestPropertyNames) {
            value =
                    requestValue(request, propertyName);

            if (value != null && !isBlank(value)) {
                break;
            }
        }

        if (value != null && !isBlank(value)) {
            sql.append(clause);
            params.put(paramName, parseDate(String.valueOf(value)));
        }
    }

    private Object requestValue(
            Object request,
            String propertyName) {

        if (request == null
                || propertyName == null
                || propertyName.isBlank()) {
            return null;
        }

        String suffix =
                Character.toUpperCase(propertyName.charAt(0))
                        + propertyName.substring(1);

        for (String methodName :
                List.of("get" + suffix, "is" + suffix)) {

            try {
                Method method =
                        request.getClass().getMethod(methodName);

                return method.invoke(request);
            } catch (Exception ignore) {
            }
        }

        try {
            Field field =
                    request.getClass().getDeclaredField(propertyName);

            field.setAccessible(true);

            return field.get(request);
        } catch (Exception ignore) {
        }

        return null;
    }

    private boolean isBlank(
            Object value) {

        return value instanceof String
                && ((String) value).isBlank();
    }

    private long toCentsLong(
            Object value) {

        if (value == null) {
            return 0L;
        }

        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).longValueExact();
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        String text =
                String.valueOf(value);

        if (text.isBlank()) {
            return 0L;
        }

        return new BigDecimal(text).longValueExact();
    }

    private void setProperty(
            Object target,
            String propertyName,
            Object value) {

        if (target == null
                || propertyName == null
                || propertyName.isBlank()) {
            return;
        }

        String suffix =
                Character.toUpperCase(propertyName.charAt(0))
                        + propertyName.substring(1);

        String setterName =
                "set" + suffix;

        for (Method method : target.getClass().getMethods()) {
            if (!method.getName().equals(setterName)
                    || method.getParameterCount() != 1) {
                continue;
            }

            try {
                method.invoke(
                        target,
                        convertValue(
                                value,
                                method.getParameterTypes()[0]));
                return;
            } catch (Exception ignore) {
            }
        }
    }

    private Object convertValue(
            Object value,
            Class<?> targetType) {

        if (value == null) {
            return null;
        }

        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }

        if (targetType == String.class) {
            return String.valueOf(value);
        }

        if (targetType == Long.class
                || targetType == Long.TYPE) {
            return toCentsLong(value);
        }

        if (targetType == Integer.class
                || targetType == Integer.TYPE) {
            return (int) toCentsLong(value);
        }

        if (targetType == BigDecimal.class) {
            if (value instanceof BigDecimal) {
                return value;
            }

            return BigDecimal.valueOf(
                    toCentsLong(value));
        }

        if (targetType == LocalDate.class) {
            if (value instanceof java.sql.Date) {
                return ((java.sql.Date) value).toLocalDate();
            }

            return parseDate(String.valueOf(value));
        }

        return value;
    }

    private LocalDate parseDate(
            String raw) {

        if (raw == null || raw.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date value is required");
        }

        try {
            return LocalDate.parse(
                    raw,
                    DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception ignore) {
        }

        try {
            return OffsetDateTime.parse(raw).toLocalDate();
        } catch (Exception ignore) {
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid date format: " + raw);
    }

    @SuppressWarnings("unchecked")
    private List<TransactionCurrencyDto> loadCurrencyRowsForEdit(
            Long transNumber) {

        List<Object[]> rows =
                entityManager.createNativeQuery(
                                "SELECT tc.denom_number, d.description, d.denom_value, tc.count, tc.count_machine, tc.amount "
                                        + "FROM gsi.transaction_currency tc "
                                        + "LEFT JOIN gsi.denomination d ON d.denom_number = tc.denom_number "
                                        + "WHERE tc.trans_number = :transNumber "
                                        + "ORDER BY COALESCE(d.display_order, tc.denom_number)")
                        .setParameter("transNumber", transNumber)
                        .getResultList();

        List<TransactionCurrencyDto> result =
                new ArrayList<>();

        for (Object[] row : rows) {
            TransactionCurrencyDto dto =
                    new TransactionCurrencyDto();

            dto.setDenomination(toInteger(row[0]));
            dto.setDescription(row[1] == null ? null : String.valueOf(row[1]));
            dto.setDenomValue(toLong(row[2]));
            dto.setCount(toInteger(row[3]));
            dto.setMachineCount(toInteger(row[4]));
            dto.setAmount(toLong(row[5]));

            result.add(dto);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private List<TransactionTicketDto> loadTicketRowsForEdit(
            Long transNumber) {

        List<Object[]> rows =
                entityManager.createNativeQuery(
                                "SELECT ticket_id, amount "
                                        + "FROM gsi.transaction_ticket "
                                        + "WHERE trans_number = :transNumber "
                                        + "ORDER BY ticket_id")
                        .setParameter("transNumber", transNumber)
                        .getResultList();

        List<TransactionTicketDto> result =
                new ArrayList<>();

        for (Object[] row : rows) {
            TransactionTicketDto dto =
                    new TransactionTicketDto();

            dto.setNumber(row[0] == null ? null : String.valueOf(row[0]));
            dto.setAmount(toLong(row[1]));

            result.add(dto);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private List<TransactionEditParamDto> loadParameterRowsForEdit(
            Long transNumber) {

        List<Object[]> rows =
                entityManager.createNativeQuery(
                                "SELECT c.param_number, c.param_name, c.param_label, c.param_required, "
                                        + "c.param_size, c.validation_proc, v.param_value "
                                        + "FROM gsi.trans_params_config c "
                                        + "LEFT JOIN gsi.transaction_param_values v "
                                        + "ON v.param_number = c.param_number "
                                        + "AND v.trans_number = :transNumber "
                                        + "WHERE COALESCE(c.param_inuse, 0) = 1 "
                                        + "ORDER BY COALESCE(c.display_order, c.param_number)")
                        .setParameter("transNumber", transNumber)
                        .getResultList();

        List<TransactionEditParamDto> result =
                new ArrayList<>();

        for (Object[] row : rows) {
            TransactionEditParamDto dto =
                    new TransactionEditParamDto();

            dto.setParamNumber(toInteger(row[0]));
            dto.setParamName(row[1] == null ? null : String.valueOf(row[1]));
            dto.setParamLabel(row[2] == null ? null : String.valueOf(row[2]));
            dto.setParamRequired(toInteger(row[3]));
            dto.setParamSize(toInteger(row[4]));
            dto.setValidationProc(row[5] == null ? null : String.valueOf(row[5]));
            dto.setValue(row[6] == null ? "" : String.valueOf(row[6]));

            result.add(dto);
        }

        return result;
    }

    /**
     * Saves edited transaction parameter values using upsert behavior.
     *
     * The transaction edit flow creates a new transaction and marks the original
     * transaction as EDITED. In some flows, parameter rows may already exist for
     * the newly saved transaction, or the request can contain the same parameter
     * more than once. The table gsi.transaction_param_values has a primary key on
     * (trans_number, param_number), so insert-only logic can fail with a duplicate
     * key error.
     *
     * Behavior:
     * - if the parameter row exists, update param_value
     * - if the parameter row does not exist, insert it
     */
    private void saveParameterRowsForEdit(
            Long transNumber,
            List<TransactionEditParamDto> parameters) {

        if (transNumber == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction number is required to save transaction parameters");
        }

        if (parameters == null) {
            return;
        }

        for (TransactionEditParamDto parameter : parameters) {
            if (parameter == null
                    || parameter.getParamNumber() == null) {
                continue;
            }

            String value =
                    parameter.getValue() == null
                            ? ""
                            : parameter.getValue().trim();

            if (value.isBlank()) {
                continue;
            }

            upsertTransactionParamValue(
                    transNumber,
                    parameter.getParamNumber(),
                    value);
        }
    }

    /**
     * Upserts one transaction parameter row.
     *
     * This avoids the duplicate primary-key failure:
     * PK_transaction_param_values, duplicate key (trans_number, param_number).
     */
    private void upsertTransactionParamValue(
            Long transNumber,
            Integer paramNumber,
            String paramValue) {

        int updatedRows =
                entityManager.createNativeQuery(
                                "UPDATE gsi.transaction_param_values "
                                        + "SET param_value = :paramValue "
                                        + "WHERE trans_number = :transNumber "
                                        + "AND param_number = :paramNumber")
                        .setParameter("transNumber", transNumber)
                        .setParameter("paramNumber", paramNumber)
                        .setParameter("paramValue", paramValue)
                        .executeUpdate();

        if (updatedRows > 0) {
            return;
        }

        entityManager.createNativeQuery(
                        "INSERT INTO gsi.transaction_param_values "
                                + "(trans_number, param_number, param_value) "
                                + "VALUES (:transNumber, :paramNumber, :paramValue)")
                .setParameter("transNumber", transNumber)
                .setParameter("paramNumber", paramNumber)
                .setParameter("paramValue", paramValue)
                .executeUpdate();
    }


    private TransactionSaveResponse buildSaveResponse(
            TransactionEntity header) {

        TransactionSaveResponse response =
                new TransactionSaveResponse();

        response.setTransNumber(
                header.getTransNumber());

        response.setStatus(
                header.getStatus());

        response.setTotalCurrency(
                header.getTotalCurrency());

        response.setTicketCount(
                header.getTicketCount());

        response.setTotalTicketAmount(
                header.getTotalTktAmount());

        response.setTotalAmount(
                header.getTotalAmount());

        return response;
    }

    private Integer toInteger(
            Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.valueOf(
                String.valueOf(value));
    }

    private Long toLong(
            Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(
                String.valueOf(value));
    }


    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
