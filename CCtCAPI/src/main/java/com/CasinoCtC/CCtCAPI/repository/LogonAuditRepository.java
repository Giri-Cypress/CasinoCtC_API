package com.CasinoCtC.CCtCAPI.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.CasinoCtC.CCtCAPI.entity.LogonAuditEntity;

@Repository
public interface LogonAuditRepository extends JpaRepository<LogonAuditEntity, Long> {
    @Query(value = """
            SELECT TOP 1 * FROM gsi.logon_audit
            WHERE location_number = :locationNumber
              AND user_number = :userNumber
              AND device_name IS NOT NULL
              AND LTRIM(RTRIM(device_name)) <> ''
            ORDER BY logon_datetime DESC, lh_number DESC
            """, nativeQuery = true)
    Optional<LogonAuditEntity> findLatestWithDevice(@Param("locationNumber") Integer locationNumber, @Param("userNumber") Integer userNumber);
    Optional<LogonAuditEntity> findTopByLocationNumberAndUserNumberAndLogonOrderByLogonDatetimeDescLhNumberDesc(Integer locationNumber, Integer userNumber, Integer logon);
}
