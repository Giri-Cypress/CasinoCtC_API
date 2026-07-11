package com.CasinoCtC.CCtCAPI.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CasinoCtC.CCtCAPI.dto.DailySummaryReportRequest;
import com.CasinoCtC.CCtCAPI.dto.DailySummaryReportRow;
import com.CasinoCtC.CCtCAPI.dto.InventoryReportRequest;
import com.CasinoCtC.CCtCAPI.dto.InventoryReportRow;

@Service
public class ReportService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<DailySummaryReportRow> getDailySummaryReport(
            DailySummaryReportRequest request,
            String printedByUserName) {

        StringBuilder sql = new StringBuilder("""
            SELECT
                t.location_number,
                t.business_date,
                t.user_number,
                t.trans_number,
                t.status,
                t.total_currency,
                t.ticket_count,
                t.total_amount
            FROM gsi.transactions t
            WHERE t.status = 'PROCESSED'
        """);

        Map<String, Object> params = new LinkedHashMap<>();

        if (request != null) {
            if (request.getLocationNumberFrom() != null) {
                sql.append(" AND t.location_number >= :locationNumberFrom");
                params.put("locationNumberFrom", request.getLocationNumberFrom());
            }

            if (request.getLocationNumberTo() != null) {
                sql.append(" AND t.location_number <= :locationNumberTo");
                params.put("locationNumberTo", request.getLocationNumberTo());
            }

            if (request.getBusinessDateFrom() != null) {
                sql.append(" AND t.business_date >= :businessDateFrom");
                params.put("businessDateFrom", request.getBusinessDateFrom());
            }

            if (request.getBusinessDateTo() != null) {
                sql.append(" AND t.business_date <= :businessDateTo");
                params.put("businessDateTo", request.getBusinessDateTo());
            }

            if (request.getUserNumberFrom() != null) {
                sql.append(" AND t.user_number >= :userNumberFrom");
                params.put("userNumberFrom", request.getUserNumberFrom());
            }

            if (request.getUserNumberTo() != null) {
                sql.append(" AND t.user_number <= :userNumberTo");
                params.put("userNumberTo", request.getUserNumberTo());
            }
        }

        sql.append(" ORDER BY t.location_number, t.business_date, t.user_number, t.trans_number");

        Query query = entityManager.createNativeQuery(sql.toString());
        setParameters(query, params);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        List<DailySummaryReportRow> response = new ArrayList<>();

        for (Object[] row : rows) {
            DailySummaryReportRow dto = new DailySummaryReportRow();
            dto.setLocationNumber(toInteger(row[0]));
            dto.setBusinessDate(toDateString(row[1]));
            dto.setUserNumber(toInteger(row[2]));
            dto.setTransactionNumber(toLong(row[3]));
            dto.setStatus(row[4] == null ? null : String.valueOf(row[4]));
            dto.setCurrencyTotal(toBigDecimal(row[5]));
            dto.setTicketCount(toBigDecimal(row[6]));
            dto.setGrandTotal(toBigDecimal(row[7]));
            response.add(dto);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<InventoryReportRow> getInventoryReport(
            InventoryReportRequest request,
            String printedByUserName) {

        StringBuilder sql = new StringBuilder("""
            SELECT
                i.location_number,
                i.user_number,
                i.cash_amt,
                d.denom_number,
                d.amount
            FROM gsi.inventory i
            LEFT JOIN gsi.inventory_dtl d
                ON d.location_number = i.location_number
               AND d.user_number = i.user_number
            WHERE 1 = 1
        """);

        Map<String, Object> params = new LinkedHashMap<>();

        if (request != null) {
            if (request.getLocationNumberFrom() != null) {
                sql.append(" AND i.location_number >= :locationNumberFrom");
                params.put("locationNumberFrom", request.getLocationNumberFrom());
            }

            if (request.getLocationNumberTo() != null) {
                sql.append(" AND i.location_number <= :locationNumberTo");
                params.put("locationNumberTo", request.getLocationNumberTo());
            }

            if (request.getUserNumberFrom() != null) {
                sql.append(" AND i.user_number >= :userNumberFrom");
                params.put("userNumberFrom", request.getUserNumberFrom());
            }

            if (request.getUserNumberTo() != null) {
                sql.append(" AND i.user_number <= :userNumberTo");
                params.put("userNumberTo", request.getUserNumberTo());
            }
        }

        sql.append(" ORDER BY i.location_number, i.user_number, d.denom_number");

        Query query = entityManager.createNativeQuery(sql.toString());
        setParameters(query, params);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        List<InventoryReportRow> response = new ArrayList<>();

        Integer currentLocationNumber = null;
        Integer currentUserNumber = null;
        long currentUserTotal = 0L;
        long currentLocationTotal = 0L;
        boolean hasCurrentUser = false;
        boolean hasCurrentLocation = false;

        for (Object[] row : rows) {
            Integer locationNumber = toInteger(row[0]);
            Integer userNumber = toInteger(row[1]);
            Long cashAmount = toLong(row[2]);
            Integer denominationNumber = toInteger(row[3]);
            Long denominationAmount = toLong(row[4]);

            if (hasCurrentUser
                    && (!sameValue(currentLocationNumber, locationNumber)
                    || !sameValue(currentUserNumber, userNumber))) {
                response.add(buildInventoryTotalRow(
                        currentLocationNumber,
                        currentUserNumber,
                        "USER_TOTAL",
                        "User Total",
                        currentUserTotal));

                currentUserTotal = 0L;
                hasCurrentUser = false;
            }

            if (hasCurrentLocation
                    && !sameValue(currentLocationNumber, locationNumber)) {
                response.add(buildInventoryTotalRow(
                        currentLocationNumber,
                        null,
                        "LOCATION_TOTAL",
                        "Location Total",
                        currentLocationTotal));

                currentLocationTotal = 0L;
                hasCurrentLocation = false;
            }

            InventoryReportRow dto = new InventoryReportRow();
            dto.setLocationNumber(locationNumber);
            dto.setUserNumber(userNumber);
            dto.setCashAmount(cashAmount);
            dto.setDenominationNumber(denominationNumber);
            dto.setDenominationAmount(denominationAmount);

            setOptionalProperty(dto, "rowType", "DETAIL");
            setOptionalProperty(dto, "totalLabel", null);
            setOptionalProperty(dto, "isTotalRow", false);

            response.add(dto);

            long lineAmount = denominationAmount != null
                    ? denominationAmount
                    : (cashAmount == null ? 0L : cashAmount);

            currentLocationNumber = locationNumber;
            currentUserNumber = userNumber;
            currentUserTotal += lineAmount;
            currentLocationTotal += lineAmount;
            hasCurrentUser = true;
            hasCurrentLocation = true;
        }

        if (hasCurrentUser) {
            response.add(buildInventoryTotalRow(
                    currentLocationNumber,
                    currentUserNumber,
                    "USER_TOTAL",
                    "User Total",
                    currentUserTotal));
        }

        if (hasCurrentLocation) {
            response.add(buildInventoryTotalRow(
                    currentLocationNumber,
                    null,
                    "LOCATION_TOTAL",
                    "Location Total",
                    currentLocationTotal));
        }

        return response;
    }

    private InventoryReportRow buildInventoryTotalRow(
            Integer locationNumber,
            Integer userNumber,
            String rowType,
            String totalLabel,
            Long totalAmount) {

        InventoryReportRow totalRow = new InventoryReportRow();
        totalRow.setLocationNumber(locationNumber);
        totalRow.setUserNumber(userNumber);
        totalRow.setCashAmount(totalAmount);
        totalRow.setDenominationNumber(null);
        totalRow.setDenominationAmount(totalAmount);

        /*
         * These optional properties allow the Angular report to render footer
         * rows differently when the DTO has matching fields. Reflection keeps
         * this service compatible even if the DTO only contains the original
         * location/user/denomination amount fields.
         */
        setOptionalProperty(totalRow, "rowType", rowType);
        setOptionalProperty(totalRow, "totalLabel", totalLabel);
        setOptionalProperty(totalRow, "denominationDescription", totalLabel);
        setOptionalProperty(totalRow, "description", totalLabel);
        setOptionalProperty(totalRow, "isTotalRow", true);

        return totalRow;
    }

    private boolean sameValue(Object left, Object right) {
        if (left == null) {
            return right == null;
        }

        return left.equals(right);
    }

    private void setOptionalProperty(Object target, String propertyName, Object value) {
        if (target == null || propertyName == null || propertyName.isBlank()) {
            return;
        }

        String suffix = Character.toUpperCase(propertyName.charAt(0))
                + propertyName.substring(1);
        String setterName = "set" + suffix;

        for (Method method : target.getClass().getMethods()) {
            if (!method.getName().equals(setterName)
                    || method.getParameterCount() != 1) {
                continue;
            }

            try {
                method.invoke(target, convertOptionalValue(
                        value,
                        method.getParameterTypes()[0]));
                return;
            } catch (Exception ignore) {
                return;
            }
        }
    }

    private Object convertOptionalValue(Object value, Class<?> targetType) {
        if (value == null) {
            if (targetType == Boolean.TYPE) {
                return false;
            }

            if (targetType == Integer.TYPE) {
                return 0;
            }

            if (targetType == Long.TYPE) {
                return 0L;
            }

            return null;
        }

        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }

        if (targetType == String.class) {
            return String.valueOf(value);
        }

        if (targetType == Boolean.class || targetType == Boolean.TYPE) {
            return Boolean.valueOf(String.valueOf(value));
        }

        if (targetType == Integer.class || targetType == Integer.TYPE) {
            if (value instanceof Number number) {
                return number.intValue();
            }

            return Integer.valueOf(String.valueOf(value));
        }

        if (targetType == Long.class || targetType == Long.TYPE) {
            if (value instanceof Number number) {
                return number.longValue();
            }

            return Long.valueOf(String.valueOf(value));
        }

        if (targetType == BigDecimal.class) {
            return toBigDecimal(value);
        }

        return value;
    }


    @Transactional(readOnly = true)
    public boolean isInventoryClearEnabled(Integer locationNumber) {
        if (locationNumber == null) {
            return false;
        }

        /*
         * Local Config overrides Global Config.
         * Accepted active values: yes, y, true, 1, enabled.
         * Accepted labels include:
         * - clear_inventory
         * - clear inventory
         * - clearinventory
         * - inventory_clear
         */
        String localSql = """
            SELECT TOP 1 lc_value
              FROM gsi.local_config
             WHERE lc_location = :locationNumber
               AND LOWER(REPLACE(lc_label, ' ', '_')) IN (
                    'clear_inventory',
                    'clearinventory',
                    'inventory_clear'
               )
               AND COALESCE(lc_status, 1) = 1
             ORDER BY lc_number
        """;

        Query localQuery = entityManager.createNativeQuery(localSql);
        localQuery.setParameter("locationNumber", locationNumber);

        List<?> localResult = localQuery.getResultList();

        if (!localResult.isEmpty()) {
            return isEnabledConfigValue(localResult.get(0));
        }

        String globalSql = """
            SELECT TOP 1 gc_value
              FROM gsi.global_config
             WHERE LOWER(REPLACE(gc_label, ' ', '_')) IN (
                    'clear_inventory',
                    'clearinventory',
                    'inventory_clear'
               )
               AND COALESCE(gc_status, 1) = 1
             ORDER BY gc_number
        """;

        List<?> globalResult =
                entityManager
                        .createNativeQuery(globalSql)
                        .getResultList();

        if (globalResult.isEmpty()) {
            return false;
        }

        return isEnabledConfigValue(globalResult.get(0));
    }

    private boolean isEnabledConfigValue(Object value) {
        if (value == null) {
            return false;
        }

        String normalizedValue =
                String.valueOf(value)
                        .trim();

        return "yes".equalsIgnoreCase(normalizedValue)
                || "y".equalsIgnoreCase(normalizedValue)
                || "true".equalsIgnoreCase(normalizedValue)
                || "1".equals(normalizedValue)
                || "enabled".equalsIgnoreCase(normalizedValue);
    }

    private void setParameters(Query query, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
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

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(String.valueOf(value));
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }

        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }

        return new BigDecimal(String.valueOf(value));
    }

    private String toDateString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
