package com.CasinoCtC.CCtCAPI.dao;

import com.CasinoCtC.CCtCAPI.dto.CurrencyDTO;
import com.CasinoCtC.CCtCAPI.dto.TicketDTO;
import com.CasinoCtC.CCtCAPI.dto.TransactionRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Repository
@RequiredArgsConstructor
public class TransactionDAOImpl
        implements TransactionDAO {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Object getTransactionById(Long id) {

        String sql = """
            SELECT
                t.TransactionID,
                t.BusinessDate,
                t.CollectionDate,
                t.BoxNumber,
                u.UserName,
                t.Status,
                t.TotalCurrency,
                t.TotalTktAmount,
                t.TotalAmount
            FROM GSI.[TRANSACTION] t
            JOIN GSI.USERS u
                ON t.UserID = u.UserID
            WHERE t.TransactionID = ?
        """;

        return jdbcTemplate.queryForMap(
            sql,
            id
        );
    }    
    
    
    @Override
    public Long saveTransaction(

            TransactionRequest request,
            Integer userId,
            Integer locationId

    ) {

      try {
    	System.out.println("✅ DAO reached");
        /*
         * ==========================================
         * 1. SAVE TRANSACTION HEADER
         * ==========================================
         */

        String transactionSql = """

            INSERT INTO GSI.[TRANSACTION]
            (
                BusinessDate,
                CollectionDate,
                BoxNumber,
                LocationID,
                UserID,
                Status,
                TotalCurrency,
                TicketCount,
                TotalTktAmount,
                TotalAmount
            )
            VALUES
            (
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
            )

        """;

        BigDecimal totalCash =
                request.getCash()
                        .stream()
                        .map(c -> BigDecimal.valueOf(
                                c.getAmount()
                        ))
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalTickets =
                request.getTickets()
                        .stream()
                        .map(t -> BigDecimal.valueOf(
                                t.getAmount()
                        ))
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal grandTotal =
                totalCash.add(totalTickets);

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps =
                    connection.prepareStatement(
                            transactionSql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            LocalDate businessDate =
                    Instant.parse(
                            request.getHeader()
                                    .getBusinessDate()
                    )
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate collectionDate =
                    Instant.parse(
                            request.getHeader()
                                    .getCollectionDate()
                    )
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            ps.setDate(
                    1,
                    Date.valueOf(businessDate)
            );

            ps.setDate(
                    2,
                    Date.valueOf(collectionDate)
            );

            ps.setString(
                    3,
                    request.getHeader()
                            .getBoxNumber()
            );

            ps.setInt(4, locationId);

            ps.setInt(5, userId);

            ps.setString(6, "SUBMITTED");

            ps.setBigDecimal(7, totalCash);

            ps.setInt(
                    8,
                    request.getTickets().size()
            );

            ps.setBigDecimal(9, totalTickets);

            ps.setBigDecimal(10, grandTotal);

            return ps;

        }, keyHolder);

        Long transactionId =
                keyHolder.getKey().longValue();

        /*
         * ==========================================
         * 2. SAVE CURRENCY ROWS
         * ==========================================
         */

        String currencySql = """

            INSERT INTO
            GSI.TRANSACTION_CURRENCY
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
                ?, ?, ?, ?, ?, ?
            )

        """;

        for (CurrencyDTO cash :
                request.getCash()) {

            jdbcTemplate.update(

                    currencySql,

                    transactionId,

                    cash.getDenomination(),

                    1,

                    "MACHINE-1",

                    cash.getCount()
                            +
                            cash.getMachineCount(),

                    cash.getAmount()

            );

        }

        /*
         * ==========================================
         * 3. SAVE TICKET ROWS
         * ==========================================
         */

        String ticketSql = """

            INSERT INTO
            GSI.TRANSACTION_TICKET
            (
                TransactionID,
                TicketID,
                Amount
            )
            VALUES
            (
                ?, ?, ?
            )

        """;

        for (TicketDTO ticket :
                request.getTickets()) {

            jdbcTemplate.update(

                    ticketSql,

                    transactionId,

                    ticket.getNumber(),

                    ticket.getAmount()

            );

        }

        return transactionId;
      } catch (Exception ex) {
		  
		System.out.println("❌ ERROR SAVING TRANSACTION");

		ex.printStackTrace();

		throw ex;

	  }
    }

}