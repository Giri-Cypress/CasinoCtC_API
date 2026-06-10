package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;

public interface RoleDAO {
    List<String> findRolesByUserId(Integer userId);
}

