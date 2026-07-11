package com.CasinoCtC.CCtCAPI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.RoleMenuEntity;
import com.CasinoCtC.CCtCAPI.entity.RoleMenuId;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenuEntity, RoleMenuId> {
    List<RoleMenuEntity> findByIdRoleNumberOrderByIdMenuNumberAsc(Integer roleNumber);
    void deleteByIdRoleNumber(Integer roleNumber);
}
