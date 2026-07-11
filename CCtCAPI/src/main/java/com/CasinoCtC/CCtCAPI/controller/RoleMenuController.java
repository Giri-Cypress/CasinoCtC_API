package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.CasinoCtC.CCtCAPI.model.RoleMenu;
import com.CasinoCtC.CCtCAPI.service.RoleMenuService;

@RestController
@RequestMapping("/api/role-menus")
@PreAuthorize("hasRole('ADMIN')")
public class RoleMenuController {

    private final RoleMenuService roleMenuJpaService;

    public RoleMenuController(RoleMenuService roleMenuJpaService) {
        this.roleMenuJpaService = roleMenuJpaService;
    }

    @GetMapping("/{roleNumber}")
    public List<RoleMenu> getRoleMenus(@PathVariable Integer roleNumber) {
        return roleMenuJpaService.getByRoleNumber(roleNumber);
    }

    @PostMapping("/{roleNumber}")
    public void saveRoleMenus(@PathVariable Integer roleNumber, @RequestBody List<RoleMenu> list) {
        roleMenuJpaService.saveRoleMenus(roleNumber, list);
    }
}
