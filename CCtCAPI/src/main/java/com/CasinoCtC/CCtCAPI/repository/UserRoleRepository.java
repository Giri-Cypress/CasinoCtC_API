package com.CasinoCtC.CCtCAPI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.UserRoleEntity;
import com.CasinoCtC.CCtCAPI.entity.UserRoleId;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
    List<UserRoleEntity> findByIdUserNumberOrderByIdRoleNumberAsc(Integer userNumber);
    void deleteByIdUserNumber(Integer userNumber);
}
