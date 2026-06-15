package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;

import com.CasinoCtC.CCtCAPI.model.Role;

public interface RoleDAO {
    List<String> findRolesByUserId(Integer userId);

    List<Role> findAllRoles();

    List<Integer> findRoleIdsByUserId(Integer userId);

    void saveUserRoles(Integer userId, List<Integer> roleIds);

    void deleteUserRoles(Integer userId);
}
