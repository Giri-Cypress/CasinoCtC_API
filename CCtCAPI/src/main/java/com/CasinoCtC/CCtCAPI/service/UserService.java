package com.CasinoCtC.CCtCAPI.service;

import java.util.List;

import com.CasinoCtC.CCtCAPI.dto.UserSaveRequest;
import com.CasinoCtC.CCtCAPI.model.User;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(int id);
    User saveUser(UserSaveRequest request);
    void deleteUser(int id);
    List<Integer> getUserRoleIds(int userId);
}