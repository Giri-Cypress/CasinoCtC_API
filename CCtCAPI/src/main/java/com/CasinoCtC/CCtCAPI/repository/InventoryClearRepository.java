package com.CasinoCtC.CCtCAPI.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InventoryClearRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<InventoryHeaderRow> findInventoryHeaders(Integer locationFrom, Integer locationTo, Integer userFrom, Integer userTo) {
        String sql = """
            SELECT i.location_number, i.user_number, COALESCE(i.cash_amt, 0) AS cash_amt
              FROM gsi.inventory i
             WHERE (:locationFrom IS NULL OR i.location_number >= :locationFrom)
               AND (:locationTo IS NULL OR i.location_number <= :locationTo)
               AND (:userFrom IS NULL OR i.user_number >= :userFrom)
               AND (:userTo IS NULL OR i.user_number <= :userTo)
             ORDER BY i.location_number, i.user_number
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("locationFrom", locationFrom);
        query.setParameter("locationTo", locationTo);
        query.setParameter("userFrom", userFrom);
        query.setParameter("userTo", userTo);

        List<?> rows = query.getResultList();
        List<InventoryHeaderRow> result = new ArrayList<>();
        for (Object rowObject : rows) {
            Object[] row = (Object[]) rowObject;
            result.add(new InventoryHeaderRow(
                    ((Number) row[0]).intValue(),
                    ((Number) row[1]).intValue(),
                    ((Number) row[2]).longValue()
            ));
        }
        return result;
    }

    public List<InventoryDetailRow> findInventoryDetails(Integer locationNumber, Integer userNumber) {
        String sql = """
            SELECT d.location_number, d.user_number, d.denom_number, COALESCE(d.amount, 0) AS amount
              FROM gsi.inventory_dtl d
             WHERE d.location_number = :locationNumber
               AND d.user_number = :userNumber
             ORDER BY d.denom_number
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("locationNumber", locationNumber);
        query.setParameter("userNumber", userNumber);

        List<?> rows = query.getResultList();
        List<InventoryDetailRow> result = new ArrayList<>();
        for (Object rowObject : rows) {
            Object[] row = (Object[]) rowObject;
            result.add(new InventoryDetailRow(
                    ((Number) row[0]).intValue(),
                    ((Number) row[1]).intValue(),
                    ((Number) row[2]).intValue(),
                    ((Number) row[3]).longValue()
            ));
        }
        return result;
    }

    public int deleteInventoryDetails(Integer locationFrom, Integer locationTo, Integer userFrom, Integer userTo) {
        String sql = """
            DELETE FROM gsi.inventory_dtl
             WHERE (:locationFrom IS NULL OR location_number >= :locationFrom)
               AND (:locationTo IS NULL OR location_number <= :locationTo)
               AND (:userFrom IS NULL OR user_number >= :userFrom)
               AND (:userTo IS NULL OR user_number <= :userTo)
            """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("locationFrom", locationFrom);
        query.setParameter("locationTo", locationTo);
        query.setParameter("userFrom", userFrom);
        query.setParameter("userTo", userTo);
        return query.executeUpdate();
    }

    public int deleteInventoryHeaders(Integer locationFrom, Integer locationTo, Integer userFrom, Integer userTo) {
        String sql = """
            DELETE FROM gsi.inventory
             WHERE (:locationFrom IS NULL OR location_number >= :locationFrom)
               AND (:locationTo IS NULL OR location_number <= :locationTo)
               AND (:userFrom IS NULL OR user_number >= :userFrom)
               AND (:userTo IS NULL OR user_number <= :userTo)
            """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("locationFrom", locationFrom);
        query.setParameter("locationTo", locationTo);
        query.setParameter("userFrom", userFrom);
        query.setParameter("userTo", userTo);
        return query.executeUpdate();
    }

    public boolean isClearInventoryEnabled(Integer locationNumber) {
        /*
          Clear Inventory can be configured at location level first.
          Lookup order:
          1) gsi.local_config for the current location (lc_location)
          2) gsi.global_config only when no active local row exists

          Expected enabled values: Yes/Y/True/1.
        */
        String localSql = """
            SELECT TOP 1 lc_value
              FROM gsi.local_config
             WHERE lc_location = :locationNumber
               AND LOWER(lc_label) = 'clear inventory'
               AND LOWER(lc_group) IN ('system', 'inventory')
               AND COALESCE(lc_status, 1) = 1
             ORDER BY lc_number
            """;

        Query localQuery = entityManager.createNativeQuery(localSql);
        localQuery.setParameter("locationNumber", locationNumber);
        List<?> localResult = localQuery.getResultList();

        if (!localResult.isEmpty()) {
            return isEnabledValue(localResult.get(0));
        }

        String globalSql = """
            SELECT TOP 1 gc_value
              FROM gsi.global_config
             WHERE LOWER(gc_label) = 'clear inventory'
               AND LOWER(gc_group) IN ('system', 'inventory')
               AND COALESCE(gc_status, 1) = 1
             ORDER BY gc_number
            """;

        Query globalQuery = entityManager.createNativeQuery(globalSql);
        List<?> globalResult = globalQuery.getResultList();

        if (globalResult.isEmpty()) {
            return false;
        }

        return isEnabledValue(globalResult.get(0));
    }

    private boolean isEnabledValue(Object value) {
        if (value == null) {
            return false;
        }

        String normalizedValue = String.valueOf(value).trim();
        return "yes".equalsIgnoreCase(normalizedValue)
                || "y".equalsIgnoreCase(normalizedValue)
                || "true".equalsIgnoreCase(normalizedValue)
                || "1".equals(normalizedValue);
    }

    public static class InventoryHeaderRow {
        private final Integer locationNumber;
        private final Integer userNumber;
        private final Long cashAmt;

        public InventoryHeaderRow(Integer locationNumber, Integer userNumber, Long cashAmt) {
            this.locationNumber = locationNumber;
            this.userNumber = userNumber;
            this.cashAmt = cashAmt;
        }

        public Integer getLocationNumber() { return locationNumber; }
        public Integer getUserNumber() { return userNumber; }
        public Long getCashAmt() { return cashAmt; }
    }

    public static class InventoryDetailRow {
        private final Integer locationNumber;
        private final Integer userNumber;
        private final Integer denomNumber;
        private final Long amount;

        public InventoryDetailRow(Integer locationNumber, Integer userNumber, Integer denomNumber, Long amount) {
            this.locationNumber = locationNumber;
            this.userNumber = userNumber;
            this.denomNumber = denomNumber;
            this.amount = amount;
        }

        public Integer getLocationNumber() { return locationNumber; }
        public Integer getUserNumber() { return userNumber; }
        public Integer getDenomNumber() { return denomNumber; }
        public Long getAmount() { return amount; }
    }
}
