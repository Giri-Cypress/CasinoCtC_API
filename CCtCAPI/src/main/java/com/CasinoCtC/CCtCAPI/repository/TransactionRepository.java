package com.CasinoCtC.CCtCAPI.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.TransactionEntity;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

	Optional<TransactionEntity>
	findFirstByUserNumberAndStatusOrderByCreatedAtDesc(
	        Integer userNumber,
	        String status
	);

}
