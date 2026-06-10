package com.CasinoCtC.CCtCAPI.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogDAOImpl
        implements AuditLogDAO {

    private final JdbcTemplate jdbcTemplate;

    public AuditLogDAOImpl(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void log(
            String username,
            String eventType,
            String eventDescription,
            String requestURI,
            String httpMethod,
            String ipAddress,
            String status) {

        String sql = """
            INSERT INTO GSI.AUDIT_LOG
            (
                Username,
                EventType,
                EventDescription,
                RequestURI,
                HttpMethod,
                IPAddress,
                Status
            )
            VALUES
            (
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?
            )
        """;

        jdbcTemplate.update(
                sql,
                username,
                eventType,
                eventDescription,
                requestURI,
                httpMethod,
                ipAddress,
                status
        );
    }
}