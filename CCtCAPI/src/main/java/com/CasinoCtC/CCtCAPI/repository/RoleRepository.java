package com.CasinoCtC.CCtCAPI.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    List<RoleEntity> findByRoleNumberInOrderByRoleNumberAsc(Collection<Integer> roleNumbers);
}
