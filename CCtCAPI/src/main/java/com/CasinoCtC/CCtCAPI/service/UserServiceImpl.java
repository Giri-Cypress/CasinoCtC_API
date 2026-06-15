package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.dao.RoleDAO;
import com.CasinoCtC.CCtCAPI.dao.UserDAO;
import com.CasinoCtC.CCtCAPI.dto.UserSaveRequest;
import com.CasinoCtC.CCtCAPI.model.User;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserDAO userDAO,
            RoleDAO roleDAO,
            PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return userDAO.findAll().stream()
                .map(this::sanitizeUser)
                .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error getting all users", ex);
            throw ex;
        }
    }

    @Override
    public User getUserById(int id) {
        try {
            return userDAO.findById(id)
                .map(this::sanitizeUser)
                .orElse(null);
        } catch (Exception ex) {
            log.error("Error getting user by id={}", id, ex);
            throw ex;
        }
    }

    @Override
    public User saveUser(UserSaveRequest request) {
        try {
            if (request.getUserId() == null) {
                throw new RuntimeException("User ID is required");
            }

            if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
                throw new RuntimeException("User Name is required");
            }

            if (request.getLocationId() == null) {
                throw new RuntimeException("Location is required");
            }

            User toSave = new User();
            toSave.setUserId(request.getUserId());
            toSave.setUserName(request.getUserName().trim());
            toSave.setLocationId(request.getLocationId());
            toSave.setStatus(request.getStatus() == null ? 1 : request.getStatus());

            User existing = userDAO.findById(request.getUserId()).orElse(null);

            // password behavior
            if (existing == null) {
                if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                    throw new RuntimeException("Password is required for a new user");
                }
                toSave.setPassword(passwordEncoder.encode(request.getPassword().trim()));
            } else {
                if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                    toSave.setPassword(existing.getPassword());
                } else {
                    toSave.setPassword(passwordEncoder.encode(request.getPassword().trim()));
                }
            }

            User saved = userDAO.save(toSave);

            // save selected roles to USER_ROLES
            roleDAO.saveUserRoles(saved.getUserId(), request.getRoleIds());

            return sanitizeUser(saved);

        } catch (Exception ex) {
            log.error("Error saving user with userId={}", request.getUserId(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteUser(int id) {
        try {
            roleDAO.deleteUserRoles(id);
            userDAO.deleteById(id);
        } catch (Exception ex) {
            log.error("Error deleting user id={}", id, ex);
            throw ex;
        }
    }

    @Override
    public List<Integer> getUserRoleIds(int userId) {
        try {
            return roleDAO.findRoleIdsByUserId(userId);
        } catch (Exception ex) {
            log.error("Error getting role IDs for userId={}", userId, ex);
            throw ex;
        }
    }

    private User sanitizeUser(User user) {
        if (user == null) return null;

        User clean = new User();
        clean.setUserId(user.getUserId());
        clean.setUserName(user.getUserName());
        clean.setPassword("");
        clean.setLocationId(user.getLocationId());
        clean.setStatus(user.getStatus());
        return clean;
    }
}