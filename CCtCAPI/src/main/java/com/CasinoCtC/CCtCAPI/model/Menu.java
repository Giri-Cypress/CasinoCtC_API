package com.CasinoCtC.CCtCAPI.model;

public class Menu {

    private Integer id;
    private String name;
    private String route;
    private Integer parentID;

    public Menu() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
    
    public Integer getParentMenuID() {
        return parentID;
    }

    public void setParentMenuID(Integer id) {
        this.parentID = id;
    }
}