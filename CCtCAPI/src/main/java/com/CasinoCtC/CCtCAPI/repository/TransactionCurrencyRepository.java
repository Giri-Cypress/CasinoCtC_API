package com.CasinoCtC.CCtCAPI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.TransactionCurrencyEntity;
import com.CasinoCtC.CCtCAPI.entity.TransactionCurrencyId;

@Repository
public interface TransactionCurrencyRepository extends JpaRepository<TransactionCurrencyEntity, TransactionCurrencyId> {
    List<TransactionCurrencyEntity> findByIdTransNumberOrderByIdDenomNumberAsc(Long transNumber);
}
