package com.CasinoCtC.CCtCAPI.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.CasinoCtC.CCtCAPI.entity.OverrideRuleEntity;
import com.CasinoCtC.CCtCAPI.entity.UserEntity;
import com.CasinoCtC.CCtCAPI.repository.OverrideRuleRepository;
import com.CasinoCtC.CCtCAPI.repository.UserRepository;
import com.CasinoCtC.CCtCAPI.repository.UserRoleRepository;

@Service
public class OverrideValidationService {

    private final OverrideRuleRepository overrideRuleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public OverrideValidationService(
            OverrideRuleRepository overrideRuleRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder) {
        this.overrideRuleRepository = overrideRuleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validateCashMismatchOverride(
            String overrideUserName,
            String overridePassword) {

        OverrideRuleEntity rule = overrideRuleRepository.findById(1)
                .orElse(null);

        if (rule == null || rule.getOverrideRoleNumber() == null) {
            // No override role configured for rule 1
            return;
        }

        if (overrideUserName == null || overrideUserName.isBlank()
                || overridePassword == null || overridePassword.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Override required for cash total mismatch");
        }

        UserEntity overrideUser = userRepository.findByUserName(overrideUserName.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Override user not found"));

        if (overrideUser.getStatus() == null || overrideUser.getStatus() == 0) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Override user is inactive");
        }

        if (!passwordEncoder.matches(overridePassword, overrideUser.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Invalid override credentials");
        }

        List<Integer> roleNumbers = userRoleRepository
                .findByIdUserNumberOrderByIdRoleNumberAsc(overrideUser.getUserNumber())
                .stream()
                .map(x -> x.getRoleNumber())
                .toList();

        if (!roleNumbers.contains(rule.getOverrideRoleNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Override user does not have required role");
        }
    }
}