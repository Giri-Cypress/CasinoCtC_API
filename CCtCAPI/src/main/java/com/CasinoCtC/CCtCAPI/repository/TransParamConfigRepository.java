package com.CasinoCtC.CCtCAPI.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.CasinoCtC.CCtCAPI.entity.TransParamConfigEntity;

public interface TransParamConfigRepository extends JpaRepository<TransParamConfigEntity,Integer> {
    List<TransParamConfigEntity> findByParamInuseOrderByDisplayOrder(Integer paramInuse);
}
