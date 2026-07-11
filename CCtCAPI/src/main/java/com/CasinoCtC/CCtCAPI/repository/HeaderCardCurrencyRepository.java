package com.CasinoCtC.CCtCAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CasinoCtC.CCtCAPI.entity.HeaderCardCurrencyEntity;
import com.CasinoCtC.CCtCAPI.entity.HeaderCardCurrencyId;

public interface HeaderCardCurrencyRepository extends JpaRepository<HeaderCardCurrencyEntity, HeaderCardCurrencyId> {
    @Modifying
    @Query("delete from HeaderCardCurrencyEntity c where c.hcAssignNumber = :hcAssignNumber")
    void deleteByHcAssignNumber(@Param("hcAssignNumber") Long hcAssignNumber);
}
