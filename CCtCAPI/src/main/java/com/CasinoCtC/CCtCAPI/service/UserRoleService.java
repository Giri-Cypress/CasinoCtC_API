package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CasinoCtC.CCtCAPI.entity.UserRoleEntity;
import com.CasinoCtC.CCtCAPI.entity.UserRoleId;
import com.CasinoCtC.CCtCAPI.repository.UserRoleRepository;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public List<Integer> getRoleNumbersByUserNumber(Integer userNumber) {
        return userRoleRepository.findByIdUserNumberOrderByIdRoleNumberAsc(userNumber)
                .stream().map(UserRoleEntity::getRoleNumber).collect(Collectors.toList());
    }

    @Transactional
    public void saveUserRoles(Integer userNumber, List<Integer> roleNumbers) {
        userRoleRepository.deleteByIdUserNumber(userNumber);
        if (roleNumbers == null || roleNumbers.isEmpty()) return;
        userRoleRepository.saveAll(roleNumbers.stream()
                .map(roleNumber -> new UserRoleEntity(new UserRoleId(userNumber, roleNumber)))
                .collect(Collectors.toList()));
    }

    @Transactional
    public void deleteUserRoles(Integer userNumber) {
        userRoleRepository.deleteByIdUserNumber(userNumber);
    }
}
