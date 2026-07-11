package com.CasinoCtC.CCtCAPI.model;

public class Menu {
    private Integer menuNumber;
    private String menuKey;
    private String route;
    private Integer parentMenuNumber;
    private Integer displayOrder;
    private Integer status;

    public Integer getMenuNumber() { return menuNumber; }
    public void setMenuNumber(Integer menuNumber) { this.menuNumber = menuNumber; }

    public String getMenuKey() { return menuKey; }
    public void setMenuKey(String menuKey) { this.menuKey = menuKey; }

    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }

    public Integer getParentMenuNumber() { return parentMenuNumber; }
    public void setParentMenuNumber(Integer parentMenuNumber) { this.parentMenuNumber = parentMenuNumber; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
