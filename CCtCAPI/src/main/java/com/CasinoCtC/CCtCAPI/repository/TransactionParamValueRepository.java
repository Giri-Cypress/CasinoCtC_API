package com.CasinoCtC.CCtCAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.CasinoCtC.CCtCAPI.entity.TransactionParamValueEntity;
import com.CasinoCtC.CCtCAPI.entity.TransactionParamValueId;

public interface TransactionParamValueRepository
    extends JpaRepository<TransactionParamValueEntity, TransactionParamValueId> {
	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = """
	    MERGE gsi.transaction_param_values AS target
	    USING (
	        SELECT
	            CAST(:transNumber AS BIGINT) AS trans_number,
	            CAST(:paramNumber AS INT) AS param_number,
	            CAST(:paramValue AS VARCHAR(50)) AS param_value
	    ) AS source
	    ON target.trans_number = source.trans_number
	       AND target.param_number = source.param_number
	    WHEN MATCHED THEN
	        UPDATE SET param_value = source.param_value
	    WHEN NOT MATCHED THEN
	        INSERT (trans_number, param_number, param_value)
	        VALUES (source.trans_number, source.param_number, source.param_value);
	    """, nativeQuery = true)
	void upsertParamValue(
	    @Param("transNumber") Long transNumber,
	    @Param("paramNumber") Integer paramNumber,
	    @Param("paramValue") String paramValue
	);
}
