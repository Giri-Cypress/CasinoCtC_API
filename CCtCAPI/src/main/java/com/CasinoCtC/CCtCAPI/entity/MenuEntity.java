package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "menus", schema = "gsi")
public class MenuEntity {

    @Id
    @Column(name = "menu_number")
    private Integer menuNumber;

    @Column(name = "menu_key", nullable = false, length = 100)
    private String menuKey;

    @Column(name = "route", length = 200)
    private String route;

    @Column(name = "parent_menu_number")
    private Integer parentMenuNumber;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "status", nullable = false)
    private Integer status;

    public Integer getMenuNumber() {
        return menuNumber;
    }

    public void setMenuNumber(Integer menuNumber) {
        this.menuNumber = menuNumber;
    }

    public String getMenuKey() {
        return menuKey;
    }

    public void setMenuKey(String menuKey) {
        this.menuKey = menuKey;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Integer getParentMenuNumber() {
        return parentMenuNumber;
    }

    public void setParentMenuNumber(Integer parentMenuNumber) {
        this.parentMenuNumber = parentMenuNumber;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
