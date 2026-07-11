package com.CasinoCtC.CCtCAPI.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CasinoCtC.CCtCAPI.dto.GlobalConfigResponse;
import com.CasinoCtC.CCtCAPI.dto.GlobalConfigUpdateRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class GlobalConfigService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<GlobalConfigResponse> getGlobalConfig() {
        String configSql = """
            SELECT
                gc_number,
                gc_group,
                gc_label,
                gc_value,
                gc_status
            FROM gsi.global_config
            WHERE gc_status = 1
            ORDER BY gc_group, gc_label, gc_number
        """;

        String optionsSql = """
            SELECT
                gc_group,
                gc_label,
                gc_value
            FROM gsi.global_config_options
            WHERE gc_status = 1
            ORDER BY gc_group, gc_label, gc_value
        """;

        @SuppressWarnings("unchecked")
        List<Object[]> configRows = entityManager.createNativeQuery(configSql).getResultList();

        @SuppressWarnings("unchecked")
        List<Object[]> optionRows = entityManager.createNativeQuery(optionsSql).getResultList();

        Map<String, List<String>> optionsByGroupAndLabel = new LinkedHashMap<>();
        for (Object[] row : optionRows) {
            String group = toStringValue(row[0]);
            String label = toStringValue(row[1]);
            String value = toStringValue(row[2]);
            String key = buildKey(group, label);
            optionsByGroupAndLabel
                .computeIfAbsent(key, k -> new ArrayList<>())
                .add(value);
        }

        List<GlobalConfigResponse> response = new ArrayList<>();
        for (Object[] row : configRows) {
            GlobalConfigResponse dto = new GlobalConfigResponse();
            dto.setGcNumber(toLong(row[0]));
            dto.setGcGroup(toStringValue(row[1]));
            dto.setGcLabel(toStringValue(row[2]));
            dto.setGcValue(toStringValue(row[3]));
            dto.setGcStatus(toInteger(row[4]));
            dto.setOptions(optionsByGroupAndLabel.getOrDefault(
                buildKey(dto.getGcGroup(), dto.getGcLabel()),
                new ArrayList<>()
            ));
            response.add(dto);
        }

        return response;
    }

    @Transactional
    public void updateGlobalConfig(List<GlobalConfigUpdateRequest> request) {
        if (request == null || request.isEmpty()) {
            throw new IllegalArgumentException("No global configuration values were supplied.");
        }

        // Deduplicate by gcNumber while preserving the last value supplied by the UI.
        Map<Long, String> valuesByGcNumber = request.stream()
            .filter(row -> row != null && row.getGcNumber() != null)
            .collect(Collectors.toMap(
                GlobalConfigUpdateRequest::getGcNumber,
                row -> normalizeValue(row.getGcValue()),
                (oldValue, newValue) -> newValue,
                LinkedHashMap::new
            ));

        if (valuesByGcNumber.isEmpty()) {
            throw new IllegalArgumentException("At least one valid gcNumber is required.");
        }

        for (Map.Entry<Long, String> entry : valuesByGcNumber.entrySet()) {
            Long gcNumber = entry.getKey();
            String gcValue = entry.getValue();
            validateValue(gcValue);

            Query query = entityManager.createNativeQuery("""
                UPDATE gsi.global_config
                SET
                    gc_value = :gcValue,
                    updated_at = GETDATE()
                WHERE gc_number = :gcNumber
                  AND gc_status = 1
            """);
            query.setParameter("gcValue", gcValue);
            query.setParameter("gcNumber", gcNumber);

            int rowsUpdated = query.executeUpdate();
            if (rowsUpdated == 0) {
                throw new IllegalArgumentException("Global configuration row was not found or is inactive: " + gcNumber);
            }
        }
    }

    private void validateValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Global configuration value is required.");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Global configuration value cannot exceed 50 characters.");
        }
    }

    private String normalizeValue(String value) {
        return value == null ? "" : value.trim();
    }

    private String buildKey(String group, String label) {
        return (group == null ? "" : group.trim().toUpperCase())
            + "|"
            + (label == null ? "" : label.trim().toUpperCase());
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(String.valueOf(value));
    }

    private String toStringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
