package com.CasinoCtC.CCtCAPI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.MenuEntity;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {

    List<MenuEntity> findAllByOrderByParentMenuNumberAscDisplayOrderAscMenuNumberAsc();

    @Query(value = """
        SELECT DISTINCT
            m.menu_number,
            m.menu_key,
            m.route,
            m.parent_menu_number,
            m.display_order,
            m.status
        FROM gsi.menus m
        INNER JOIN gsi.role_menus rm ON rm.menu_number = m.menu_number
        INNER JOIN gsi.user_roles ur ON ur.role_number = rm.role_number
        WHERE ur.user_number = :userNumber
          AND rm.security_level > 0
          AND m.status = 1
        ORDER BY m.parent_menu_number, m.display_order, m.menu_number
        """, nativeQuery = true)
    List<MenuEntity> findAccessibleMenusByUserNumber(@Param("userNumber") Integer userNumber);
}
