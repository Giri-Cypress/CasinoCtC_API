package com.CasinoCtC.CCtCAPI.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.TransactionHeaderEntity;

@Repository
public interface TransactionHeaderRepository
        extends JpaRepository<TransactionHeaderEntity, Integer> {

    Optional<TransactionHeaderEntity>
    findFirstByBusinessDateAndCollectionDateAndBoxNumberAndStatus(
            LocalDate businessDate,
            LocalDate collectionDate,
            String boxNumber,
            String status);

    Optional<TransactionHeaderEntity>
    findFirstByProcessUserAndStatusOrderByTransNumberDesc(
            String processUser,
            String status);

    long deleteByProcessUser(String processUser);

    long deleteByProcessUserAndStatus(
            String processUser,
            String status);
}