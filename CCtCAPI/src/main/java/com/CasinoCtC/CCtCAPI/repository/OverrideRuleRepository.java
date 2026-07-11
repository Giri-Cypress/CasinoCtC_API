package com.CasinoCtC.CCtCAPI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.OverrideRuleEntity;

@Repository
public interface OverrideRuleRepository
        extends JpaRepository<OverrideRuleEntity, Integer> {

    List<OverrideRuleEntity> findAllByOrderByRuleNumberAsc();
}