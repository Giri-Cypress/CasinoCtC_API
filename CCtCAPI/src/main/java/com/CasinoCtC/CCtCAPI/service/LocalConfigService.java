package com.CasinoCtC.CCtCAPI.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.CasinoCtC.CCtCAPI.dto.LocalConfigLocationContext;
import com.CasinoCtC.CCtCAPI.dto.LocalConfigLocationOption;
import com.CasinoCtC.CCtCAPI.dto.LocalConfigResponse;
import com.CasinoCtC.CCtCAPI.dto.LocalConfigUpdateRequest;
import com.CasinoCtC.CCtCAPI.entity.UserEntity;
import com.CasinoCtC.CCtCAPI.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class LocalConfigService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;

    public LocalConfigService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public LocalConfigLocationContext getLocationContext(String userName, boolean canManageAllLocations) {
        Integer currentLocationNumber = resolveLocationNumber(userName);
        LocalConfigLocationContext context = new LocalConfigLocationContext();
        context.setCurrentLocationNumber(currentLocationNumber);
        context.setCanManageAllLocations(canManageAllLocations);
        context.setLocations(canManageAllLocations ? getAllActiveLocations() : List.of(getLocationOption(currentLocationNumber)));
        return context;
    }

    @Transactional(readOnly = true)
    public List<LocalConfigResponse> getLocalConfig(String userName, Integer requestedLocationNumber, boolean canManageAllLocations) {
        Integer locationNumber = resolveRequestedLocationNumber(userName, requestedLocationNumber, canManageAllLocations);
        Map<String, List<String>> optionsByGroupAndLabel = getOptionsByGroupAndLabel();

        @SuppressWarnings("unchecked")
        List<Object[]> localRows = entityManager.createNativeQuery("""
            SELECT lc_number, lc_location, lc_group, lc_label, lc_value, lc_status
            FROM gsi.local_config
            WHERE lc_status = 1
              AND lc_location = :locationNumber
            ORDER BY lc_group, lc_label, lc_number
        """)
            .setParameter("locationNumber", locationNumber)
            .getResultList();

        if (!localRows.isEmpty()) {
            return mapLocalRows(localRows, optionsByGroupAndLabel, false);
        }

        // No local rows exist for the selected location yet.
        // Show active global config rows as defaults so the user can save them into local_config.
        @SuppressWarnings("unchecked")
        List<Object[]> globalRows = entityManager.createNativeQuery("""
            SELECT gc_group, gc_label, gc_value, gc_status
            FROM gsi.global_config
            WHERE gc_status = 1
            ORDER BY gc_group, gc_label, gc_number
        """).getResultList();

        List<LocalConfigResponse> response = new ArrayList<>();
        for (Object[] row : globalRows) {
            LocalConfigResponse dto = new LocalConfigResponse();
            dto.setLcNumber(null);
            dto.setLcLocation(locationNumber);
            dto.setLcGroup(toStringValue(row[0]));
            dto.setLcLabel(toStringValue(row[1]));
            dto.setLcValue(toStringValue(row[2]));
            dto.setLcStatus(toInteger(row[3]));
            dto.setDefaultFromGlobal(true);
            dto.setOptions(optionsByGroupAndLabel.getOrDefault(buildKey(dto.getLcGroup(), dto.getLcLabel()), new ArrayList<>()));
            response.add(dto);
        }
        return response;
    }

    @Transactional
    public void updateLocalConfig(String userName, Integer requestedLocationNumber, boolean canManageAllLocations, List<LocalConfigUpdateRequest> request) {
        Integer locationNumber = resolveRequestedLocationNumber(userName, requestedLocationNumber, canManageAllLocations);
        if (request == null || request.isEmpty()) {
            throw new IllegalArgumentException("No local configuration values were supplied.");
        }

        for (LocalConfigUpdateRequest row : request) {
            if (row == null) {
                continue;
            }

            String lcValue = normalizeValue(row.getLcValue());
            validateValue(lcValue);

            if (row.getLcNumber() != null) {
                updateByLcNumber(locationNumber, row.getLcNumber(), lcValue);
            } else {
                upsertByLocationGroupLabel(locationNumber, row.getLcGroup(), row.getLcLabel(), lcValue);
            }
        }
    }

    private List<LocalConfigResponse> mapLocalRows(List<Object[]> rows, Map<String, List<String>> optionsByGroupAndLabel, boolean defaultFromGlobal) {
        List<LocalConfigResponse> response = new ArrayList<>();
        for (Object[] row : rows) {
            LocalConfigResponse dto = new LocalConfigResponse();
            dto.setLcNumber(toLong(row[0]));
            dto.setLcLocation(toInteger(row[1]));
            dto.setLcGroup(toStringValue(row[2]));
            dto.setLcLabel(toStringValue(row[3]));
            dto.setLcValue(toStringValue(row[4]));
            dto.setLcStatus(toInteger(row[5]));
            dto.setDefaultFromGlobal(defaultFromGlobal);
            dto.setOptions(optionsByGroupAndLabel.getOrDefault(buildKey(dto.getLcGroup(), dto.getLcLabel()), new ArrayList<>()));
            response.add(dto);
        }
        return response;
    }

    private Map<String, List<String>> getOptionsByGroupAndLabel() {
        @SuppressWarnings("unchecked")
        List<Object[]> optionRows = entityManager.createNativeQuery("""
            SELECT gc_group, gc_label, gc_value
            FROM gsi.global_config_options
            WHERE gc_status = 1
            ORDER BY gc_group, gc_label, gc_value
        """).getResultList();

        Map<String, List<String>> optionsByGroupAndLabel = new LinkedHashMap<>();
        for (Object[] row : optionRows) {
            String key = buildKey(toStringValue(row[0]), toStringValue(row[1]));
            optionsByGroupAndLabel.computeIfAbsent(key, k -> new ArrayList<>()).add(toStringValue(row[2]));
        }
        return optionsByGroupAndLabel;
    }

    private void updateByLcNumber(Integer locationNumber, Long lcNumber, String lcValue) {
        Query query = entityManager.createNativeQuery("""
            UPDATE gsi.local_config
            SET lc_value = :lcValue,
                updated_at = GETDATE()
            WHERE lc_number = :lcNumber
              AND lc_location = :locationNumber
              AND lc_status = 1
        """);
        query.setParameter("lcValue", lcValue);
        query.setParameter("lcNumber", lcNumber);
        query.setParameter("locationNumber", locationNumber);

        int rowsUpdated = query.executeUpdate();
        if (rowsUpdated == 0) {
            throw new IllegalArgumentException("Local configuration row was not found for the selected location or is inactive: " + lcNumber);
        }
    }

    private void upsertByLocationGroupLabel(Integer locationNumber, String lcGroup, String lcLabel, String lcValue) {
        String group = normalizeRequired(lcGroup, "Local configuration group is required.");
        String label = normalizeRequired(lcLabel, "Local configuration label is required.");

        Query update = entityManager.createNativeQuery("""
            UPDATE gsi.local_config
            SET lc_value = :lcValue,
                updated_at = GETDATE()
            WHERE lc_location = :locationNumber
              AND lc_group = :lcGroup
              AND lc_label = :lcLabel
              AND lc_status = 1
        """);
        update.setParameter("lcValue", lcValue);
        update.setParameter("locationNumber", locationNumber);
        update.setParameter("lcGroup", group);
        update.setParameter("lcLabel", label);

        int rowsUpdated = update.executeUpdate();
        if (rowsUpdated > 0) {
            return;
        }

        Query insert = entityManager.createNativeQuery("""
            INSERT INTO gsi.local_config
                (lc_location, lc_group, lc_label, lc_value, lc_status, created_at, updated_at)
            VALUES
                (:locationNumber, :lcGroup, :lcLabel, :lcValue, 1, GETDATE(), GETDATE())
        """);
        insert.setParameter("locationNumber", locationNumber);
        insert.setParameter("lcGroup", group);
        insert.setParameter("lcLabel", label);
        insert.setParameter("lcValue", lcValue);
        insert.executeUpdate();
    }

    private Integer resolveRequestedLocationNumber(String userName, Integer requestedLocationNumber, boolean canManageAllLocations) {
        Integer currentLocationNumber = resolveLocationNumber(userName);
        if (requestedLocationNumber == null || requestedLocationNumber.equals(currentLocationNumber)) {
            return requestedLocationNumber == null ? currentLocationNumber : requestedLocationNumber;
        }
        if (!canManageAllLocations) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not authorized to manage local configuration for other locations.");
        }
        verifyActiveLocationExists(requestedLocationNumber);
        return requestedLocationNumber;
    }

    private Integer resolveLocationNumber(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user name was not found.");
        }
        UserEntity user = userRepository.findByUserName(userName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user was not found: " + userName));
        if (user.getLocationNumber() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authenticated user does not have a location number: " + userName);
        }
        return user.getLocationNumber();
    }

    private List<LocalConfigLocationOption> getAllActiveLocations() {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery("""
            SELECT location_number, location_name
            FROM gsi.locations
            WHERE status = 1
            ORDER BY location_name, location_number
        """).getResultList();
        List<LocalConfigLocationOption> locations = new ArrayList<>();
        for (Object[] row : rows) {
            locations.add(new LocalConfigLocationOption(toInteger(row[0]), toStringValue(row[1])));
        }
        return locations;
    }

    private LocalConfigLocationOption getLocationOption(Integer locationNumber) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery("""
            SELECT location_number, location_name
            FROM gsi.locations
            WHERE location_number = :locationNumber
              AND status = 1
        """).setParameter("locationNumber", locationNumber).getResultList();
        if (rows.isEmpty()) {
            return new LocalConfigLocationOption(locationNumber, String.valueOf(locationNumber));
        }
        Object[] row = rows.get(0);
        return new LocalConfigLocationOption(toInteger(row[0]), toStringValue(row[1]));
    }

    private void verifyActiveLocationExists(Integer locationNumber) {
        Object count = entityManager.createNativeQuery("""
            SELECT COUNT(1)
            FROM gsi.locations
            WHERE location_number = :locationNumber
              AND status = 1
        """).setParameter("locationNumber", locationNumber).getSingleResult();
        Long countValue = toLong(count);
        if (countValue == null || countValue == 0L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected location is not active: " + locationNumber);
        }
    }

    private void validateValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Local configuration value is required.");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Local configuration value cannot exceed 50 characters.");
        }
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeValue(value);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String normalizeValue(String value) { return value == null ? "" : value.trim(); }
    private String buildKey(String group, String label) { return normalizeValue(group).toUpperCase() + "|" + normalizeValue(label).toUpperCase(); }
    private Long toLong(Object value) { if (value == null) return null; if (value instanceof Number number) return number.longValue(); return Long.valueOf(String.valueOf(value)); }
    private Integer toInteger(Object value) { if (value == null) return null; if (value instanceof Number number) return number.intValue(); return Integer.valueOf(String.valueOf(value)); }
    private String toStringValue(Object value) { return value == null ? "" : String.valueOf(value); }
}
