package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.model.Menu;
import com.CasinoCtC.CCtCAPI.mapper.MenuRowMapper;

import lombok.RequiredArgsConstructor;

@Repository

public class MenuDAOImpl implements MenuDAO {

    private final JdbcTemplate jdbcTemplate;
    private final MenuRowMapper menuRowMapper;
    
    public MenuDAOImpl (
            JdbcTemplate jdbcTemplate,		
            MenuRowMapper menuRowMapper) {
    	    this.jdbcTemplate = jdbcTemplate;
    	    this.menuRowMapper = menuRowMapper;
    }
    @Override
    public List<Menu> findMenusByUserId(Integer userId) {

        String sql = "SELECT DISTINCT m.menuid, m.menuname, m.route, m.parentmenuid FROM GSI.menus m JOIN GSI.role_menus rm ON m.menuid = rm.menuid JOIN GSI.user_roles ur ON rm.roleid = ur.roleid WHERE ur.userid= ?";
        

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> menuRowMapper.mapRow(rs, rowNum),
                userId           // ✅ parameter
        );

    }
}