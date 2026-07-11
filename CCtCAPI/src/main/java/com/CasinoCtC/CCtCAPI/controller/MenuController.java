package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.CasinoCtC.CCtCAPI.model.Menu;
import com.CasinoCtC.CCtCAPI.service.MenuService;

@RestController
@RequestMapping("/api/menus")
@PreAuthorize("hasRole('ADMIN')")
public class MenuController {

    private final MenuService menuJpaService;

    public MenuController(MenuService menuJpaService) {
        this.menuJpaService = menuJpaService;
    }

    @GetMapping
    public List<Menu> getAllMenus() {
        return menuJpaService.getAllMenus();
    }

    @GetMapping("/{menuNumber}")
    public Menu getMenuByNumber(@PathVariable Integer menuNumber) {
        return menuJpaService.getMenuByNumber(menuNumber);
    }

    @PostMapping
    public Menu saveMenu(@RequestBody Menu menu) {
        return menuJpaService.saveMenu(menu);
    }

    @DeleteMapping("/{menuNumber}")
    public void deleteMenu(@PathVariable Integer menuNumber) {
        menuJpaService.deleteMenu(menuNumber);
    }
}
