package com.CasinoCtC.CCtCAPI.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.CasinoCtC.CCtCAPI.entity.DenominationEntity;

@Repository
public interface DenominationRepository extends JpaRepository<DenominationEntity, Integer> {
    List<DenominationEntity> findByActiveOrderByDisplayOrderAscDenomNumberAsc(Integer active);
    List<DenominationEntity> findAllByOrderByDisplayOrderAscDenomNumberAsc();
}
