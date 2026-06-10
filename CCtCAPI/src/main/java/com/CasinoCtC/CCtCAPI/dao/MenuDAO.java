package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;

import com.CasinoCtC.CCtCAPI.model.Menu;

public interface MenuDAO {
    List<Menu> findMenusByUserId(Integer userId);
}
