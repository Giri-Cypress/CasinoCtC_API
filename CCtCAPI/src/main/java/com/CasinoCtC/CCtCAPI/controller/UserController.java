package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.CasinoCtC.CCtCAPI.dto.UserSaveRequest;
import com.CasinoCtC.CCtCAPI.model.User;
import com.CasinoCtC.CCtCAPI.service.UserService;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(
            UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        try {
            return userService.getAllUsers();
        } catch (Exception ex) {
            log.error("Error in GET /api/users", ex);
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        try {
            return userService.getUserById(id);
        } catch (Exception ex) {
            log.error("Error in GET /api/users/{}", id, ex);
            throw ex;
        }
    }

    @GetMapping("/{id}/roles")
    public List<Integer> getUserRoleIds(@PathVariable int id) {
        try {
            return userService.getUserRoleIds(id);
        } catch (Exception ex) {
            log.error("Error in GET /api/users/{}/roles", id, ex);
            throw ex;
        }
    }

    @PostMapping
    public User saveUser(@RequestBody UserSaveRequest request) {
        try {
            return userService.saveUser(request);
        } catch (Exception ex) {
            log.error("Error in POST /api/users for userId={}", request.getUserId(), ex);
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        try {
            userService.deleteUser(id);
        } catch (Exception ex) {
            log.error("Error in DELETE /api/users/{}", id, ex);
            throw ex;
        }
    }
}
