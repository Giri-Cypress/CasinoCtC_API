package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.model.TransactionCurrencyRequest;

@Repository
public class InventoryDAOImpl
        implements InventoryDAO {

    private final JdbcTemplate jdbcTemplate;

    public InventoryDAOImpl(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateInventory(
            Integer locationId,
            Integer userId,
            Long totalCurrency) {

        String updateSql = """
            UPDATE GSI.INVENTORY
            SET
                CashAmt = CashAmt + ?,
                UpdateDate = GETDATE()
            WHERE LocationID = ?
            AND UserID = ?
        """;

        int updated =
                jdbcTemplate.update(
                        updateSql,
                        totalCurrency,
                        locationId,
                        userId
                );

        // ✅ Insert if no row exists
        if (updated == 0) {

            String insertSql = """
                INSERT INTO GSI.INVENTORY
                (
                    LocationID,
                    UserID,
                    CashAmt
                )
                VALUES
                (
                    ?,
                    ?,
                    ?
                )
            """;

            jdbcTemplate.update(
                    insertSql,
                    locationId,
                    userId,
                    totalCurrency
            );
        }
    }

    @Override
    public void updateInventoryDetails(
            Integer locationId,
            Integer userId,
            List<TransactionCurrencyRequest>
                    currencyRows) {

        for (TransactionCurrencyRequest row
                : currencyRows) {

            String updateSql = """
                UPDATE GSI.INVENTORYDTL
                SET
                    Amount = Amount + ?,
                    UpdateDate = GETDATE()
                WHERE LocationID = ?
                AND UserID = ?
                AND DenomID = ?
            """;

            int updated =
                    jdbcTemplate.update(
                            updateSql,

                            row.getAmount(),

                            locationId,

                            userId,

                            row.getDenomID()
                    );

            // ✅ Insert if missing
            if (updated == 0) {

                String insertSql = """
                    INSERT INTO GSI.INVENTORYDTL
                    (
                        LocationID,
                        UserID,
                        DenomID,
                        Amount
                    )
                    VALUES
                    (
                        ?,
                        ?,
                        ?,
                        ?
                    )
                """;

                jdbcTemplate.update(
                        insertSql,

                        locationId,

                        userId,

                        row.getDenomID(),

                        row.getAmount()
                );
            }
        }
    }
}