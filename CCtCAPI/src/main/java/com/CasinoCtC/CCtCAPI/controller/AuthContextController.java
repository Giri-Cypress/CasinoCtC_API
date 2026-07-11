package com.CasinoCtC.CCtCAPI.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.CasinoCtC.CCtCAPI.dto.AuthUserContextResponse;
import com.CasinoCtC.CCtCAPI.entity.UserEntity;
import com.CasinoCtC.CCtCAPI.repository.LocationRepository;
import com.CasinoCtC.CCtCAPI.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthContextController {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public AuthContextController(UserRepository userRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    @GetMapping("/current-user-context")
    public AuthUserContextResponse getCurrentUserContext(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user was not found");
        }

        String userName = authentication.getName().trim();
        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User context was not found for " + userName));

        AuthUserContextResponse response = new AuthUserContextResponse();
        response.setUserNumber(user.getUserNumber());
        response.setUserName(user.getUserName());
        response.setLocationNumber(user.getLocationNumber());
        response.setLocationName(
                locationRepository.findById(user.getLocationNumber())
                        .map(location -> location.getLocationName())
                        .orElse(null));
        return response;
    }
}
