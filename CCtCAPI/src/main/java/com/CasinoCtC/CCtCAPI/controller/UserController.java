package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.CasinoCtC.CCtCAPI.model.User;
import com.CasinoCtC.CCtCAPI.service.UserService;
import com.CasinoCtC.CCtCAPI.service.UserRoleService;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userJpaService;
    private final UserRoleService userRoleJpaService;

    public UserController(UserService userJpaService, UserRoleService userRoleJpaService) {
        this.userJpaService = userJpaService;
        this.userRoleJpaService = userRoleJpaService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userJpaService.getAllUsers();
    }

    @GetMapping("/{userNumber}")
    public User getUserByNumber(@PathVariable Integer userNumber) {
        return userJpaService.getUserByNumber(userNumber);
    }

    @GetMapping("/by-username/{userName}")
    public User getUserByUserName(@PathVariable String userName) {
        return userJpaService.getUserByUserName(userName);
    }

    @GetMapping("/{userNumber}/roles")
    public List<Integer> getUserRoleNumbers(@PathVariable Integer userNumber) {
        return userRoleJpaService.getRoleNumbersByUserNumber(userNumber);
    }

    @PostMapping
    public User saveUser(@RequestBody User user) {
        return userJpaService.saveUser(user);
    }

    @PostMapping("/{userNumber}/roles")
    public void saveUserRoles(@PathVariable Integer userNumber, @RequestBody List<Integer> roleNumbers) {
        userRoleJpaService.saveUserRoles(userNumber, roleNumbers);
    }

    @DeleteMapping("/{userNumber}")
    public void deleteUser(@PathVariable Integer userNumber) {
        userJpaService.deleteUser(userNumber);
    }
}
