package com.CasinoCtC.CCtCAPI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.CollectionPointEntity;

@Repository
public interface CollectionPointRepository
        extends JpaRepository<CollectionPointEntity, Integer> {

    List<CollectionPointEntity>
    findAllByOrderByCollectionPointIdAsc();

    List<CollectionPointEntity>
    findByLocationNumberAndStatusOrderByCollectionPointName(
            Integer locationNumber,
            Integer status);

    boolean existsByLocationNumberAndStatusAndCollectionPointNameIgnoreCase(
            Integer locationNumber,
            Integer status,
            String collectionPointName);

    boolean existsByStatusAndCollectionPointNameIgnoreCase(
            Integer status,
            String collectionPointName);
}
