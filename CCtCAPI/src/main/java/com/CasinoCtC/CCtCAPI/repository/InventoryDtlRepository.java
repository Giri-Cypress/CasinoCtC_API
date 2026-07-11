package com.CasinoCtC.CCtCAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.InventoryDtlEntity;
import com.CasinoCtC.CCtCAPI.entity.InventoryDtlId;

@Repository
public interface InventoryDtlRepository extends JpaRepository<InventoryDtlEntity, InventoryDtlId> {
}
