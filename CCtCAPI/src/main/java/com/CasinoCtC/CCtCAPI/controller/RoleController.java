package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.CasinoCtC.CCtCAPI.dao.RoleDAO;
import com.CasinoCtC.CCtCAPI.model.Role;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    private final RoleDAO roleDAO;

    public RoleController(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @GetMapping
    public List<Role> getAllRoles() {
        try {
            return roleDAO.findAllRoles();
        } catch (Exception ex) {
            log.error("Error in GET /api/roles", ex);
            throw ex;
        }
    }
}