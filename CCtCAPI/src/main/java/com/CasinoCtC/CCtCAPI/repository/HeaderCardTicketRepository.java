package com.CasinoCtC.CCtCAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CasinoCtC.CCtCAPI.entity.HeaderCardTicketEntity;
import com.CasinoCtC.CCtCAPI.entity.HeaderCardTicketId;

public interface HeaderCardTicketRepository extends JpaRepository<HeaderCardTicketEntity, HeaderCardTicketId> {
    @Modifying
    @Query("delete from HeaderCardTicketEntity t where t.hcAssignNumber = :hcAssignNumber")
    void deleteByHcAssignNumber(@Param("hcAssignNumber") Long hcAssignNumber);
}
