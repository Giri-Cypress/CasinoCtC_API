package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.CasinoCtC.CCtCAPI.model.Role;
import com.CasinoCtC.CCtCAPI.service.RoleService;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleJpaService;

    public RoleController(RoleService roleJpaService) {
        this.roleJpaService = roleJpaService;
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return roleJpaService.getAllRoles();
    }

    @GetMapping("/{roleNumber}")
    public Role getRoleByNumber(@PathVariable Integer roleNumber) {
        return roleJpaService.getRoleByNumber(roleNumber);
    }

    @PostMapping
    public Role saveRole(@RequestBody Role role) {
        return roleJpaService.saveRole(role);
    }

    @DeleteMapping("/{roleNumber}")
    public void deleteRole(@PathVariable Integer roleNumber) {
        roleJpaService.deleteRole(roleNumber);
    }
}
