package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.entity.MenuEntity;
import com.CasinoCtC.CCtCAPI.model.Menu;
import com.CasinoCtC.CCtCAPI.repository.MenuRepository;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<Menu> getAllMenus() {
        return menuRepository.findAllByOrderByParentMenuNumberAscDisplayOrderAscMenuNumberAsc()
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    public Menu getMenuByNumber(Integer menuNumber) {
        return menuRepository.findById(menuNumber).map(this::toModel).orElse(null);
    }

    public Menu saveMenu(Menu menu) {
        return toModel(menuRepository.save(toEntity(menu)));
    }

    public void deleteMenu(Integer menuNumber) {
        menuRepository.deleteById(menuNumber);
    }

    private Menu toModel(MenuEntity entity) {
        Menu model = new Menu();
        model.setMenuNumber(entity.getMenuNumber());
        model.setMenuKey(entity.getMenuKey());
        model.setRoute(entity.getRoute());
        model.setParentMenuNumber(entity.getParentMenuNumber());
        model.setDisplayOrder(entity.getDisplayOrder());
        model.setStatus(entity.getStatus());
        return model;
    }

    private MenuEntity toEntity(Menu model) {
        MenuEntity entity = new MenuEntity();
        entity.setMenuNumber(model.getMenuNumber());
        entity.setMenuKey(model.getMenuKey());
        entity.setRoute(model.getRoute());
        entity.setParentMenuNumber(model.getParentMenuNumber());
        entity.setDisplayOrder(model.getDisplayOrder() == null ? 0 : model.getDisplayOrder());
        entity.setStatus(model.getStatus() == null ? 1 : model.getStatus());
        return entity;
    }
}
