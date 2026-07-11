package com.CasinoCtC.CCtCAPI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.LocationMiscEntity;
import com.CasinoCtC.CCtCAPI.entity.LocationMiscId;

@Repository
public interface LocationMiscRepository extends JpaRepository<LocationMiscEntity, LocationMiscId> {
    List<LocationMiscEntity> findByIdLocationNumberOrderByIdMiscNumberAsc(Integer locationNumber);
    void deleteByIdLocationNumber(Integer locationNumber);
}
