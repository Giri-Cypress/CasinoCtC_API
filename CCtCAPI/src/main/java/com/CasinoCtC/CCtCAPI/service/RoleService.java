package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.entity.RoleEntity;
import com.CasinoCtC.CCtCAPI.model.Role;
import com.CasinoCtC.CCtCAPI.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    public Role getRoleByNumber(Integer roleNumber) {
        return roleRepository.findById(roleNumber).map(this::toModel).orElse(null);
    }

    public Role saveRole(Role role) {
        return toModel(roleRepository.save(toEntity(role)));
    }

    public void deleteRole(Integer roleNumber) {
        roleRepository.deleteById(roleNumber);
    }

    private Role toModel(RoleEntity entity) {
        Role model = new Role();
        model.setRoleNumber(entity.getRoleNumber());
        model.setRoleName(entity.getRoleName());
        model.setStatus(entity.getStatus());
        model.setOverrideCashLimit(entity.getOverrideCashLimit());
        model.setOverrideTicketLimit(entity.getOverrideTicketLimit());
        return model;
    }

    private RoleEntity toEntity(Role model) {
        RoleEntity entity = new RoleEntity();
        entity.setRoleNumber(model.getRoleNumber());
        entity.setRoleName(model.getRoleName());
        entity.setStatus(model.getStatus() == null ? 1 : model.getStatus());
        entity.setOverrideCashLimit(model.getOverrideCashLimit());
        entity.setOverrideTicketLimit(model.getOverrideTicketLimit());
        return entity;
    }
}
