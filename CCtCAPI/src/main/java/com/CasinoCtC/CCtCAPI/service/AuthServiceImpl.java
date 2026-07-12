package com.CasinoCtC.CCtCAPI.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.CasinoCtC.CCtCAPI.entity.RefreshTokenEntity;
import com.CasinoCtC.CCtCAPI.entity.RoleEntity;
import com.CasinoCtC.CCtCAPI.entity.UserEntity;
import com.CasinoCtC.CCtCAPI.model.LoginResponse;
import com.CasinoCtC.CCtCAPI.model.Menu;
import com.CasinoCtC.CCtCAPI.repository.LocationRepository;
import com.CasinoCtC.CCtCAPI.repository.MenuRepository;
import com.CasinoCtC.CCtCAPI.repository.RefreshTokenRepository;
import com.CasinoCtC.CCtCAPI.repository.RoleRepository;
import com.CasinoCtC.CCtCAPI.repository.UserRepository;
import com.CasinoCtC.CCtCAPI.repository.UserRoleRepository;
import com.CasinoCtC.CCtCAPI.security.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final LocationRepository locationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final long refreshTokenExpirationMinutes;


    public AuthServiceImpl(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository,
            MenuRepository menuRepository,
            LocationRepository locationRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            @Value("${app.jwt.refresh-token-expiration-minutes:10080}")
            long refreshTokenExpirationMinutes) {

        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
        this.locationRepository = locationRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenExpirationMinutes = refreshTokenExpirationMinutes;
    }

    @Override
    @Transactional
    public LoginResponse login(String userName, String password) {

        if (userName == null || userName.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username is required");
        }

        if (password == null || password.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password is required");
        }

        UserEntity user = userRepository.findByUserName(userName.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid username or password"));
        


        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User is inactive");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");
        }
       


        userRepository.updateLastLoginAt(
        user.getUserNumber(),
        LocalDateTime.now());

        List<Integer> roleNumbers = userRoleRepository
                .findByIdUserNumberOrderByIdRoleNumberAsc(user.getUserNumber())
                .stream()
                .map(ur -> ur.getId().getRoleNumber())
                .toList();

        List<String> roleNames = roleRepository
                .findByRoleNumberInOrderByRoleNumberAsc(roleNumbers)
                .stream()
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toList());

        List<Menu> menus = menuRepository
                .findAccessibleMenusByUserNumber(user.getUserNumber())
                .stream()
                .map(me -> {
                    Menu m = new Menu();
                    m.setMenuNumber(me.getMenuNumber());
                    m.setMenuKey(me.getMenuKey());
                    m.setRoute(me.getRoute());
                    m.setParentMenuNumber(me.getParentMenuNumber());
                    m.setDisplayOrder(me.getDisplayOrder());
                    m.setStatus(me.getStatus());
                    return m;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        ensureInventoryArchiveReportMenu(menus);

        String accessToken =
                jwtUtil.generateAccessToken(user.getUserName(), roleNames);

        String refreshToken = UUID.randomUUID().toString();

        saveRefreshToken(user.getUserNumber(), refreshToken);

        LoginResponse response = new LoginResponse();
        response.setUserNumber(user.getUserNumber());
        response.setLocationNumber(user.getLocationNumber());
        response.setUserName(user.getUserName());
        response.setRoles(roleNames);
        response.setMenus(menus);

        response.setLocationName(
                locationRepository.findById(user.getLocationNumber())
                        .map(loc -> loc.getLocationName())
                        .orElse(null));

        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {

        String hashed = hashToken(refreshToken);

        RefreshTokenEntity stored = refreshTokenRepository
                .findByRefreshTokenHashAndRevokedFalseAndExpiryAtAfter(
                        hashed,
                        LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid refresh token"));

        UserEntity user = userRepository.findById(stored.getUserNumber())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"));

        List<Integer> roleNumbers = userRoleRepository
                .findByIdUserNumberOrderByIdRoleNumberAsc(user.getUserNumber())
                .stream()
                .map(ur -> ur.getId().getRoleNumber())
                .toList();

        List<String> roleNames = roleRepository
                .findByRoleNumberInOrderByRoleNumberAsc(roleNumbers)
                .stream()
                .map(RoleEntity::getRoleName)
                .toList();

        List<Menu> menus = menuRepository
                .findAccessibleMenusByUserNumber(user.getUserNumber())
                .stream()
                .map(me -> {
                    Menu m = new Menu();
                    m.setMenuNumber(me.getMenuNumber());
                    m.setMenuKey(me.getMenuKey());
                    m.setRoute(me.getRoute());
                    m.setParentMenuNumber(me.getParentMenuNumber());
                    m.setDisplayOrder(me.getDisplayOrder());
                    m.setStatus(me.getStatus());
                    return m;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        ensureInventoryArchiveReportMenu(menus);

        String newAccessToken =
                jwtUtil.generateAccessToken(user.getUserName(), roleNames);

        String newRefreshToken = UUID.randomUUID().toString();

        stored.setRevoked(true);
        stored.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(stored);

        saveRefreshToken(user.getUserNumber(), newRefreshToken);

        LoginResponse response = new LoginResponse();
        response.setUserNumber(user.getUserNumber());
        response.setLocationNumber(user.getLocationNumber());
        response.setUserName(user.getUserName());
        response.setRoles(roleNames);
        response.setMenus(menus);

        response.setLocationName(
                locationRepository.findById(user.getLocationNumber())
                        .map(loc -> loc.getLocationName())
                        .orElse(null));

        response.setToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);

        return response;
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {

        String hashed = hashToken(refreshToken);

        refreshTokenRepository
                .findByRefreshTokenHashAndRevokedFalseAndExpiryAtAfter(
                        hashed,
                        LocalDateTime.now())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    token.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(token);
                });
    }

    private void ensureInventoryArchiveReportMenu(List<Menu> menus) {
        if (menus == null) {
            return;
        }

        boolean hasInventoryArchiveReport = menus.stream()
                .anyMatch(menu -> "menu.inventoryArchiveReport".equals(menu.getMenuKey())
                        || "/reports/inventoryArchive".equalsIgnoreCase(String.valueOf(menu.getRoute())));

        if (hasInventoryArchiveReport) {
            return;
        }

        boolean hasReportsParent = menus.stream()
                .anyMatch(menu -> "menu.reports".equals(menu.getMenuKey())
                        || Integer.valueOf(3).equals(menu.getMenuNumber()));

        boolean hasInventoryAccess = menus.stream()
                .anyMatch(menu -> "menu.inventoryReport".equals(menu.getMenuKey())
                        || "/reports/inventory".equalsIgnoreCase(String.valueOf(menu.getRoute())));

        if (!hasReportsParent || !hasInventoryAccess) {
            return;
        }

        Menu archiveMenu = new Menu();
        archiveMenu.setMenuNumber(24);
        archiveMenu.setMenuKey("menu.inventoryArchiveReport");
        archiveMenu.setRoute("/reports/inventoryArchive");
        archiveMenu.setParentMenuNumber(3);
        archiveMenu.setDisplayOrder(3);
        archiveMenu.setStatus(1);
        menus.add(archiveMenu);
    }

    private void saveRefreshToken(
            Integer userNumber,
            String rawRefreshToken) {

        RefreshTokenEntity tokenEntity = new RefreshTokenEntity();
        tokenEntity.setUserNumber(userNumber);
        tokenEntity.setRefreshTokenHash(hashToken(rawRefreshToken));
        tokenEntity.setExpiryAt(
                LocalDateTime.now()
                        .plusMinutes(refreshTokenExpirationMinutes));
        tokenEntity.setRevoked(false);

        refreshTokenRepository.save(tokenEntity);
    }

    private String hashToken(String value) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to hash token",
                    ex);
        }
    }
}