package com.CasinoCtC.CCtCAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.InventoryEntity;
import com.CasinoCtC.CCtCAPI.entity.InventoryId;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, InventoryId> {
}
