package com.CasinoCtC.CCtCAPI.dao;

import com.CasinoCtC.CCtCAPI.dao.TransactionInquiryDAO;
import com.CasinoCtC.CCtCAPI.dto.TransactionInquiryResponse;
import com.CasinoCtC.CCtCAPI.dto.TransactionSearchRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;



@Repository
public class TransactionInquiryDAOImpl implements TransactionInquiryDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    /*
     * =========================================
     * SEARCH TRANSACTIONS (ALREADY EXISTING)
     * =========================================
     */
    @Override
    public List<TransactionInquiryResponse> searchTransactions(
            TransactionSearchRequest request) {
    	System.out.println("✅ DAO METHOD CALLED: SearchTransaction()");
        String sql = """
            SELECT
                t.TransactionID,
                t.BusinessDate,
                t.CollectionDate,
                t.BoxNumber,
                u.UserName,
                t.Status,
                t.TotalAmount
            FROM GSI.[TRANSACTION] t
            JOIN GSI.USERS u
                ON t.UserID = u.UserID
            WHERE 1 = 1
        """;

        List<Object> params = new ArrayList<>();
    	MapSqlParameterSource params1 = new MapSqlParameterSource();
    	params1.addValue("fromDate", request.getBusinessDateFrom());
    	params1.addValue("toDate", request.getBusinessDateTo());

        // ✅ Business Date From

		if (request.getBusinessDateFrom() != null) {
		    sql = sql + (" AND t.BusinessDate >= :fromBusDate");
	    	params1.addValue("fromBusDate", request.getBusinessDateFrom());
//		    params.add(request.getBusinessDateFrom());
		}


        // ✅ Business Date To

		if (request.getBusinessDateTo() != null) {
			sql = sql +(" AND t.BusinessDate <= :toBusDate");
	    	params1.addValue("toBusDate", request.getBusinessDateTo());
//			params.add(request.getBusinessDateTo());
		}
		

		// ✅ Collection Date From

		if (request.getCollectionDateFrom() != null) {
		    sql = sql + (" AND t.CollectionDate >= :fromCollctDate");
	    	params1.addValue("fromCollctDate", request.getCollectionDateFrom());
//		    params.add(request.getBusinessDateFrom());
		}


        // ✅ Collection Date To

		if (request.getCollectionDateTo() != null) {
			sql = sql +(" AND t.CollectionDate <= :toCollctDate");
	    	params1.addValue("toCollctDate", request.getCollectionDateTo());
//			params.add(request.getBusinessDateTo());
		}


        // ✅ Box Number From
        if (request.getBoxNumberFrom() != null && !request.getBoxNumberFrom().isEmpty()) {
            sql = sql + " AND t.BoxNumber >= :fromBoxNo";
	    	params1.addValue("fromBoxNo", request.getBoxNumberFrom());
//            params.add(request.getBoxNumberFrom());
        }

        // ✅ Box Number To
        if (request.getBoxNumberTo() != null && !request.getBoxNumberTo().isEmpty()) {
            sql = sql + " AND t.BoxNumber <= :toBoxNo";
	    	params1.addValue("toBoxNo", request.getBoxNumberTo());
//            params.add(request.getBoxNumberTo());
        }


        // ✅ Status
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            sql = sql + " AND t.Status = :status";
            params1.addValue("status", request.getStatus());   
            //params.add(request.getStatus());
        }
     

        // ✅ User ID (via USERS table)
        if (request.getUserId() != null ) {
            sql = sql + " AND u.UserId = ?";
            params.add(request.getUserId());
        }


        if (request.getTicketId() != null && !request.getTicketId().isEmpty()) {
            sql= sql +("""
                AND EXISTS (
                    SELECT 1
                    FROM GSI.TicketDetails td
                    WHERE td.TransactionID = t.TransactionID
                    AND td.TicketID = ?
                )
            """);
            params.add(request.getTicketId());
        }

        // ✅ Debug (optional, remove later)
        System.out.println("Final SQL: " + sql);
        System.out.println("Params: " + params);
        
        try {

        	return namedParameterJdbcTemplate.query(
        			sql,
        			params1,
        			new BeanPropertyRowMapper<TransactionInquiryResponse>(TransactionInquiryResponse.class)
        	);
		} catch (Exception e) {
		    e.printStackTrace();   // ✅ THIS WILL SHOW REAL ERROR
		    throw e;
		}

    }

    /*
     * =========================================
     * GET TRANSACTION BY ID (FINAL)
     * =========================================
     */
    @Override
    public Map<String, Object> getTransactionById(Long id) {
       	System.out.println("✅ DAO METHOD CALLED: getTransactionById");
    try {    
        Map<String, Object> result = new HashMap<>();

        /*
         * -----------------------------------------
         * HEADER QUERY
         * -----------------------------------------
         */
        String headerSql = """
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

        Map<String, Object> header =
                jdbcTemplate.queryForMap(headerSql, id);

        result.putAll(header);

        /*
         * -----------------------------------------
         * CURRENCY DETAILS
         * -----------------------------------------
         */
        String currencySql = """
            SELECT
                DenomId,
                Count,
                Amount
            FROM GSI.TRANSACTION_CURRENCY
            WHERE TransactionID = ?
            ORDER BY DenomId
        """;

        List<Map<String, Object>> currencyList =
                jdbcTemplate.queryForList(currencySql, id);

        result.put("currencyDetails", currencyList);

        /*
         * -----------------------------------------
         * TICKET DETAILS
         * -----------------------------------------
         */
        String ticketSql = """
            SELECT
                TicketID,
                Amount
            FROM GSI.TRANSACTION_TICKET
            WHERE TransactionID = ?
            ORDER BY TicketID
        """;

        List<Map<String, Object>> ticketList =
                jdbcTemplate.queryForList(ticketSql, id);

        result.put("ticketDetails", ticketList);

        /*
         * -----------------------------------------
         * FINAL RESPONSE
         * -----------------------------------------
         */
        return result;
    } catch (Exception ex) {

        // ✅ FULL ERROR LOGGING
        System.out.println("🔥 ERROR in getTransactionById()");
        System.out.println("👉 Transaction ID: " + id);
        System.out.println("👉 Error Message: " + ex.getMessage());

        ex.printStackTrace();  // ✅ VERY IMPORTANT
        throw ex;              // ✅ rethrow so frontend sees error

    	}
    
    }
}