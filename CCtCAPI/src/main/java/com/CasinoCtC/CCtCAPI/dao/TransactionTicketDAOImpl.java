package com.CasinoCtC.CCtCAPI.dao;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.model.TransactionTicketRequest;

@Repository
public class TransactionTicketDAOImpl
        implements TransactionTicketDAO {

    private static final Logger log =
            LoggerFactory.getLogger(
                    TransactionTicketDAOImpl.class
            );

    private final JdbcTemplate jdbcTemplate;

    // ✅ Constructor Injection
    public TransactionTicketDAOImpl(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertTicketRows(
            Long transactionId,
            List<TransactionTicketRequest>
                    tickets) {

        log.info(
                "Starting ticket insert for TransactionID={}",
                transactionId
        );

        // ✅ No Tickets
        if (tickets == null
                || tickets.isEmpty()) {

            log.info(
                    "No ticket rows found for TransactionID={}",
                    transactionId
            );

            return;
        }

        String sql = """
            INSERT INTO GSI.TRANSACTION_TICKET
            (
                TransactionID,
                TicketID,
                Amount
            )
            VALUES
            (
                ?,
                ?,
                ?
            )
        """;

        try {

            // ✅ Batch Insert
            jdbcTemplate.batchUpdate(

                sql,

                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(
                            PreparedStatement ps,
                            int i)
                            throws SQLException {

                        TransactionTicketRequest ticket =
                                tickets.get(i);

                        ps.setLong(
                                1,
                                transactionId
                        );

                        ps.setString(
                                2,
                                ticket.getTicketID()
                        );

                        ps.setLong(
                                3,
                                ticket.getAmount()
                        );
                    }

                    @Override
                    public int getBatchSize() {

                        return tickets.size();
                    }
                }
            );

            log.info(
                    "Inserted {} ticket rows for TransactionID={}",
                    tickets.size(),
                    transactionId
            );

        } catch (Exception ex) {

            log.error(
                    "Error inserting ticket rows for TransactionID={}",
                    transactionId,
                    ex
            );

            throw ex;
        }
    }
}
