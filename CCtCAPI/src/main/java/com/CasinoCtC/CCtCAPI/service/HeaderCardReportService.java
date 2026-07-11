package com.CasinoCtC.CCtCAPI.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CasinoCtC.CCtCAPI.dto.HeaderCardReportRow;

@Service
public class HeaderCardReportService {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public HeaderCardReportService(
            NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<HeaderCardReportRow> getUnassignedHeaderCards() {

        return namedJdbcTemplate.query(
                baseSql()
                + """
                   WHERE a.status = 0
                   ORDER BY a.business_date DESC,
                            a.hc_assign_number DESC
                   """,
                new MapSqlParameterSource(),
                rowMapper());
    }

    @Transactional(readOnly = true)
    public List<HeaderCardReportRow> getUnprocessedHeaderCards(
            LocalDate businessDate) {

        return getByStatusAndBusinessDate(1, businessDate);
    }

    @Transactional(readOnly = true)
    public List<HeaderCardReportRow> getProcessedHeaderCards(
            LocalDate businessDate) {

        return getByStatusAndBusinessDate(2, businessDate);
    }

    private List<HeaderCardReportRow> getByStatusAndBusinessDate(
            Integer status,
            LocalDate businessDate) {

        return namedJdbcTemplate.query(
                baseSql()
                + """
                   WHERE a.status = :status
                   ORDER BY a.business_date DESC,
                            a.hc_assign_number DESC
                   """,
                new MapSqlParameterSource()
                        .addValue("status", status),
                rowMapper());
    }

    private String baseSql() {

        return """
            SELECT TOP 200
                   a.hc_assign_number,
                   a.business_date,
                   a.collection_Date AS collection_date,
                   a.box_number,
                   a.header_card_ID AS header_card_id,
                   a.file_name,
                   a.machine_id,
                   a.status,
                   COALESCE(c.total_amount, 0) AS total_amount,
                   COALESCE(t.ticket_count, 0) AS ticket_count
              FROM gsi.header_card_assign a

              LEFT JOIN (
                    SELECT hc_assign_number,
                           SUM(amount) AS total_amount
                      FROM gsi.header_card_currency
                     GROUP BY hc_assign_number
              ) c
                ON c.hc_assign_number = a.hc_assign_number

              LEFT JOIN (
                    SELECT hc_assign_number,
                           COUNT(*) AS ticket_count
                      FROM gsi.header_card_tickets
                     GROUP BY hc_assign_number
              ) t
                ON t.hc_assign_number = a.hc_assign_number
        """;
    }

    private RowMapper<HeaderCardReportRow> rowMapper() {
        return (rs, rowNum) -> mapRow(rs);
    }

    private HeaderCardReportRow mapRow(ResultSet rs)
            throws SQLException {

        HeaderCardReportRow row = new HeaderCardReportRow();

        row.setHcAssignNumber(
                rs.getObject("hc_assign_number") == null
                        ? null
                        : rs.getLong("hc_assign_number"));

        row.setBusinessDate(
                toLocalDate(
                        rs.getDate("business_date")));

        row.setCollectionDate(
                toLocalDate(
                        rs.getDate("collection_date")));

        row.setBoxNumber(
                rs.getString("box_number"));

        row.setHeaderCardId(
                rs.getString("header_card_id"));

        row.setFileName(
                rs.getString("file_name"));

        row.setMachineId(
                rs.getObject("machine_id") == null
                        ? null
                        : rs.getInt("machine_id"));

        row.setStatus(
                rs.getObject("status") == null
                        ? null
                        : rs.getInt("status"));

        row.setTotalAmount(
                rs.getBigDecimal("total_amount"));

        row.setTicketCount(
                rs.getObject("ticket_count") == null
                        ? 0L
                        : rs.getLong("ticket_count"));

        return row;
    }

    private LocalDate toLocalDate(Date date) {
        return date == null
                ? null
                : date.toLocalDate();
    }
}