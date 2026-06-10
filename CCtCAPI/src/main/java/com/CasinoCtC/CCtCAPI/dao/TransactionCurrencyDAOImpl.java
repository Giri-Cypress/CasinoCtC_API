package com.CasinoCtC.CCtCAPI.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.model.TransactionCurrencyRequest;

@Repository
public class TransactionCurrencyDAOImpl
        implements TransactionCurrencyDAO {

    private final JdbcTemplate jdbcTemplate;

    public TransactionCurrencyDAOImpl(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertCurrencyRows(
            Long transactionId,
            List<TransactionCurrencyRequest>
                    currencyRows) {

        String sql = """
            INSERT INTO GSI.TRANSACTION_CURRENCY
            (
                TransactionID,
                DenomID,
                Quality,
                MachineID,
                Count,
                Amount
            )
            VALUES
            (
                ?,
                ?,
                ?,
                ?,
                ?,
                ?
            )
        """;

        jdbcTemplate.batchUpdate(

            sql,

            new BatchPreparedStatementSetter() {

                @Override
                public void setValues(
                        PreparedStatement ps,
                        int i)
                        throws SQLException {

                    TransactionCurrencyRequest row =
                            currencyRows.get(i);

                    ps.setLong(
                            1,
                            transactionId
                    );

                    ps.setShort(
                            2,
                            row.getDenomID()
                    );

                    ps.setInt(
                            3,
                            row.getQuality()
                    );

                    ps.setString(
                            4,
                            row.getMachineID()
                    );

                    ps.setInt(
                            5,
                            row.getCount()
                    );

                    ps.setLong(
                            6,
                            row.getAmount()
                    );
                }

                @Override
                public int getBatchSize() {

                    return currencyRows.size();
                }
            }
        );
    }
}