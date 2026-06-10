package com.CasinoCtC.CCtCAPI.dao;

public interface AuditLogDAO {

    void log(
            String username,
            String eventType,
            String eventDescription,
            String requestURI,
            String httpMethod,
            String ipAddress,
            String status
    );
}