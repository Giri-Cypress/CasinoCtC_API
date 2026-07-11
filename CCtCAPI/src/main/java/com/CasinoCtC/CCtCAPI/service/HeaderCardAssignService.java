package com.CasinoCtC.CCtCAPI.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.CasinoCtC.CCtCAPI.dto.HeaderCardAssignRow;
import com.CasinoCtC.CCtCAPI.dto.HeaderCardAssignUpdateRequest;

@Service
public class HeaderCardAssignService {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public HeaderCardAssignService(JdbcTemplate jdbcTemplate,
            NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<HeaderCardAssignRow> getAll() {
        return jdbcTemplate.query("""
            SELECT header_card_ID AS header_card_id,
                   business_date,
                   collection_Date AS collection_date,
                   box_number,
                   file_name,
                   status
              FROM gsi.header_card_assign
             WHERE status = 1
             ORDER BY hc_assign_number DESC
        """, rowMapper());
    }

    @Transactional(readOnly = true)
    public HeaderCardAssignRow getByHeaderCardId(Long headerCardId) {
        if (headerCardId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header Card ID is required.");
        }
        return getByHeaderCardId(String.valueOf(headerCardId));
    }

    @Transactional(readOnly = true)
    public HeaderCardAssignRow getByHeaderCardId(String headerCardId) {
        if (headerCardId == null || headerCardId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header Card ID is required.");
        }

        String headerCardIdText = headerCardId.trim();
        String normalizedHeaderCardId = normalizeHeaderCard(headerCardIdText);

        List<HeaderCardAssignRow> rows = namedJdbcTemplate.query("""
            SELECT header_card_ID AS header_card_id,
                   business_date,
                   collection_Date AS collection_date,
                   box_number,
                   file_name,
                   status
              FROM gsi.header_card_assign
             WHERE (header_card_ID = :headerCardId OR header_card_ID = :normalizedHeaderCardId)
               AND status = 1
             ORDER BY hc_assign_number DESC
        """, new MapSqlParameterSource()
                .addValue("headerCardId", headerCardIdText)
                .addValue("normalizedHeaderCardId", normalizedHeaderCardId), rowMapper());

        if (rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Header Card assignment was not found.");
        }
        return rows.get(0);
    }

    @Transactional
    public HeaderCardAssignRow update(Long currentHeaderCardId, HeaderCardAssignUpdateRequest request) {
        if (currentHeaderCardId == null || request == null || request.getHeaderCardId() == null
                || request.getHeaderCardId().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header Card ID is required.");
        }
        if (request.getBusinessDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business Date is required.");
        }
        if (request.getCollectionDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collection Date is required.");
        }
        if (request.getBoxNumber() == null || request.getBoxNumber().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Box Number is required.");
        }

        String currentHeaderCardIdText = String.valueOf(currentHeaderCardId);
        String normalizedCurrentHeaderCardId = normalizeHeaderCard(currentHeaderCardIdText);
        String newHeaderCardId = normalizeHeaderCard(request.getHeaderCardId());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currentHeaderCardId", currentHeaderCardIdText)
                .addValue("normalizedCurrentHeaderCardId", normalizedCurrentHeaderCardId)
                .addValue("newHeaderCardId", newHeaderCardId)
                .addValue("businessDate", request.getBusinessDate())
                .addValue("collectionDate", request.getCollectionDate())
                .addValue("boxNumber", request.getBoxNumber().trim());

        int updated = namedJdbcTemplate.update("""
            UPDATE gsi.header_card_assign
               SET business_date = :businessDate,
                   collection_Date = :collectionDate,
                   box_number = :boxNumber,
                   header_card_ID = :newHeaderCardId
             WHERE hc_assign_number = (
                   SELECT TOP 1 hc_assign_number
                     FROM gsi.header_card_assign
                    WHERE (header_card_ID = :currentHeaderCardId OR header_card_ID = :normalizedCurrentHeaderCardId)
                      AND status = 1
                    ORDER BY hc_assign_number DESC
             )
        """, params);

        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Header Card assignment was not found.");
        }
        return getByHeaderCardId(newHeaderCardId);
    }

    @Transactional
    public void delete(Long headerCardId) {
        if (headerCardId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header Card ID is required.");
        }

        MapSqlParameterSource params = new MapSqlParameterSource("headerCardId", headerCardId);

        // Delete is allowed for unprocessed rows. Status 0 rows may have a file_name from the batch XML load,
        // so allow status = 0 even when file_name is populated. Always block processed rows (status = 2).
        int deleted = namedJdbcTemplate.update("""
            DELETE FROM gsi.header_card_assign
             WHERE header_card_ID = :headerCardId
               AND status <> 2
               AND (file_name IS NULL OR status = 0)
        """, params);

        if (deleted == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Header Card assignment was not deleted. Delete is allowed only when status is 0, or file_name is NULL, and status is not 2.");
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUnassignedReport() {
        return namedJdbcTemplate.queryForList("""
            SELECT *
              FROM gsi.header_card_assign
             WHERE status = 0
             ORDER BY hc_assign_number DESC
        """, new MapSqlParameterSource());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUnprocessedReport(LocalDate businessDate) {
        if (businessDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business Date is required.");
        }
        return getByStatusAndProcessDate(1, businessDate);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProcessedReport(LocalDate businessDate) {
        if (businessDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business Date is required.");
        }
        return getByStatusAndProcessDate(2, businessDate);
    }

    private List<Map<String, Object>> getByStatusAndProcessDate(Integer status, LocalDate businessDate) {
        return namedJdbcTemplate.queryForList("""
            SELECT *
              FROM gsi.header_card_assign
             WHERE status = :status
               AND business_date = :businessDate
             ORDER BY business_date ASC, hc_assign_number DESC
        """, new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("businessDate", businessDate));
    }

    private String normalizeHeaderCard(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || !trimmed.matches("\\d+") || trimmed.length() >= 10) {
            return trimmed;
        }
        return String.format("%10s", trimmed).replace(' ', '0');
    }

    private RowMapper<HeaderCardAssignRow> rowMapper() {
        return (rs, rowNum) -> mapRow(rs);
    }

    private HeaderCardAssignRow mapRow(ResultSet rs) throws SQLException {
        HeaderCardAssignRow row = new HeaderCardAssignRow();
        row.setHeaderCardId(rs.getString("header_card_id"));

String hci1 = rs.getString("header_card_id");
String hci2 =  row.getHeaderCardId();

        row.setBusinessDate(toLocalDate(rs.getDate("business_date")));
        row.setCollectionDate(toLocalDate(rs.getDate("collection_date")));
        row.setBoxNumber(rs.getString("box_number"));
        row.setFileName(rs.getString("file_name"));
        row.setStatus(rs.getObject("status") == null ? null : rs.getInt("status"));
        // The header_card_assign table in this environment does not contain process_date.
        // Keep DTO field for UI compatibility and return blank/null.
        row.setProcessDate(null);
        return row;
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
