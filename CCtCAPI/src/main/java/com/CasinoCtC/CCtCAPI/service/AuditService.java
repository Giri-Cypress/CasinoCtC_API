package com.CasinoCtC.CCtCAPI.service;

import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.dao.AuditLogDAO;

@Service
public class AuditService {

    private final AuditLogDAO auditLogDAO;

    public AuditService(
            AuditLogDAO auditLogDAO) {

        this.auditLogDAO = auditLogDAO;
    }

    public void logEvent(
            String username,
            String eventType,
            String description,
            String uri,
            String method,
            String ip,
            String status) {

        auditLogDAO.log(
                username,
                eventType,
                description,
                uri,
                method,
                ip,
                status
        );
        
    }
}