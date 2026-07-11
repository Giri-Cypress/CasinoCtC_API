package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CasinoCtC.CCtCAPI.entity.RoleMenuEntity;
import com.CasinoCtC.CCtCAPI.entity.RoleMenuId;
import com.CasinoCtC.CCtCAPI.model.RoleMenu;
import com.CasinoCtC.CCtCAPI.repository.RoleMenuRepository;

@Service
public class RoleMenuService {

    private final RoleMenuRepository roleMenuRepository;

    public RoleMenuService(RoleMenuRepository roleMenuRepository) {
        this.roleMenuRepository = roleMenuRepository;
    }

    public List<RoleMenu> getByRoleNumber(Integer roleNumber) {
        return roleMenuRepository.findByIdRoleNumberOrderByIdMenuNumberAsc(roleNumber)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    @Transactional
    public void saveRoleMenus(Integer roleNumber, List<RoleMenu> list) {
        roleMenuRepository.deleteByIdRoleNumber(roleNumber);
        if (list == null || list.isEmpty()) return;
        roleMenuRepository.saveAll(list.stream().map(rm -> toEntity(roleNumber, rm)).collect(Collectors.toList()));
    }

    private RoleMenu toModel(RoleMenuEntity entity) {
        RoleMenu model = new RoleMenu();
        model.setRoleNumber(entity.getRoleNumber());
        model.setMenuNumber(entity.getMenuNumber());
        model.setSecurityLevel(entity.getSecurityLevel());
        return model;
    }

    private RoleMenuEntity toEntity(Integer roleNumber, RoleMenu model) {
        RoleMenuEntity entity = new RoleMenuEntity();
        entity.setId(new RoleMenuId(roleNumber, model.getMenuNumber()));
        entity.setSecurityLevel(model.getSecurityLevel() == null ? 1 : model.getSecurityLevel());
        return entity;
    }
}
