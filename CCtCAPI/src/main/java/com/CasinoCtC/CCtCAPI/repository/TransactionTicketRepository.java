package com.CasinoCtC.CCtCAPI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.TransactionTicketEntity;
import com.CasinoCtC.CCtCAPI.entity.TransactionTicketId;

@Repository
public interface TransactionTicketRepository extends JpaRepository<TransactionTicketEntity, TransactionTicketId> {
    List<TransactionTicketEntity> findByIdTransNumberOrderByIdTicketIdAsc(Long transNumber);
}
