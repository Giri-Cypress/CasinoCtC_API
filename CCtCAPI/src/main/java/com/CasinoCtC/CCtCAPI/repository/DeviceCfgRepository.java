package com.CasinoCtC.CCtCAPI.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.CasinoCtC.CCtCAPI.entity.DeviceCfgEntity;
import com.CasinoCtC.CCtCAPI.entity.DeviceCfgId;

@Repository
public interface DeviceCfgRepository extends JpaRepository<DeviceCfgEntity, DeviceCfgId> {
    List<DeviceCfgEntity> findAllByOrderByLocationNumberAscStationNameAscKindsAsc();
    List<DeviceCfgEntity> findByLocationNumberAndUseflgOrderByDeviceNameAscStationNameAscKindsAsc(Integer locationNumber, Integer useflg);
}
