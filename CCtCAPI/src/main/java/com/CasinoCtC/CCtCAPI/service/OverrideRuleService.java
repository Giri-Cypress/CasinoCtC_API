package com.CasinoCtC.CCtCAPI.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.CasinoCtC.CCtCAPI.dto.OverrideRuleResponse;
import com.CasinoCtC.CCtCAPI.dto.OverrideValidateResponse;
import com.CasinoCtC.CCtCAPI.entity.OverrideRuleEntity;
import com.CasinoCtC.CCtCAPI.entity.RoleEntity;
import com.CasinoCtC.CCtCAPI.entity.UserEntity;
import com.CasinoCtC.CCtCAPI.repository.OverrideRuleRepository;
import com.CasinoCtC.CCtCAPI.repository.RoleRepository;
import com.CasinoCtC.CCtCAPI.repository.UserRepository;
import com.CasinoCtC.CCtCAPI.repository.UserRoleRepository;

@Service
public class OverrideRuleService {

    private static final int CASH_RULE_NUMBER = 1;
    private static final int TICKET_RULE_NUMBER = 2;

    private final OverrideRuleRepository overrideRuleRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SupervisorActivityService supervisorActivityService;

    public OverrideRuleService(
            OverrideRuleRepository overrideRuleRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            SupervisorActivityService supervisorActivityService) {
        this.overrideRuleRepository = overrideRuleRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.supervisorActivityService = supervisorActivityService;
    }

    public List<OverrideRuleResponse> getAllRules() {
        return overrideRuleRepository.findAllByOrderByRuleNumberAsc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OverrideRuleResponse getRule(Integer ruleNumber) {
        OverrideRuleEntity entity = overrideRuleRepository.findById(ruleNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Override rule not found"));
        return toResponse(entity);
    }

    public OverrideValidateResponse validateOverride(
            List<Integer> ruleNumbers,
            String userName,
            String password,
            Integer cashDifference,
            Integer ticketDifference,
            String processUserName) {

        if (ruleNumbers == null || ruleNumbers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one rule number is required");
        }

        List<Integer> cleanedRuleNumbers = ruleNumbers.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (cleanedRuleNumbers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one valid rule number is required");
        }

        List<OverrideRuleEntity> rules = cleanedRuleNumbers.stream()
                .map(ruleNumber -> overrideRuleRepository.findById(ruleNumber)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Override rule not found: " + ruleNumber)))
                .toList();

        Set<Integer> requiredRoleNumbers = rules.stream()
                .map(OverrideRuleEntity::getOverrideRoleNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (requiredRoleNumbers.isEmpty()) {
            OverrideValidateResponse response = new OverrideValidateResponse();
            response.setValid(true);
            response.setApprovedRuleNumbers(cleanedRuleNumbers);
            response.setApprovedRoleNumbers(new ArrayList<>());
            return response;
        }

        if (userName == null || userName.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Override username and password are required");
        }

        UserEntity supervisorUser = userRepository.findByUserName(userName.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid override username or password"));

        if (supervisorUser.getStatus() == null || supervisorUser.getStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Override user is inactive");
        }

        if (!passwordEncoder.matches(password, supervisorUser.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid override username or password");
        }

        List<Integer> supervisorRoleNumbers = userRoleRepository
                .findByIdUserNumberOrderByIdRoleNumberAsc(supervisorUser.getUserNumber())
                .stream()
                .map(x -> x.getRoleNumber())
                .toList();

        List<Integer> missingRoleNumbers = requiredRoleNumbers.stream()
                .filter(roleNumber -> !supervisorRoleNumbers.contains(roleNumber))
                .toList();

        if (!missingRoleNumbers.isEmpty()) {
            String missingRoleNames = missingRoleNumbers.stream()
                    .map(this::getRoleNameOrFallback)
                    .collect(Collectors.joining(", "));
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Override user does not have required role(s): " + missingRoleNames);
        }

        Integer cashDiff = cashDifference == null ? 0 : Math.abs(cashDifference);
        Integer ticketDiff = ticketDifference == null ? 0 : Math.abs(ticketDifference);

        UserEntity processUser = null;
        if (processUserName != null && !processUserName.isBlank()) {
            processUser = userRepository.findByUserName(processUserName.trim()).orElse(null);
        }

        List<Integer> activityDifferences = new ArrayList<>();
        if (cleanedRuleNumbers.contains(CASH_RULE_NUMBER)) {
            activityDifferences.add(cashDiff);
        }
        if (cleanedRuleNumbers.contains(TICKET_RULE_NUMBER)) {
            activityDifferences.add(ticketDiff);
        }

        // Requirement: write supervisor_activity in real time when supervisor override is attempted,
        // even when the amount/count is above the configured role limit and validation fails.
        // SupervisorActivityService starts its own transaction, so this audit row is committed
        // before validateRoleLimits can throw a ResponseStatusException.
        List<Long> activityNumbers = supervisorActivityService.recordOverrideActivities(
                processUser != null ? processUser.getUserNumber() : supervisorUser.getUserNumber(),
                supervisorUser.getUserNumber(),
                activityDifferences);

        validateRoleLimits(rules, requiredRoleNumbers, cashDiff, ticketDiff);

        OverrideValidateResponse response = new OverrideValidateResponse();
        response.setValid(true);
        response.setOverrideUserName(supervisorUser.getUserName());
        response.setOverrideUserNumber(supervisorUser.getUserNumber());
        response.setApprovedRuleNumbers(cleanedRuleNumbers);
        response.setApprovedRoleNumbers(new ArrayList<>(requiredRoleNumbers));
        response.setSupervisorActivityNumbers(activityNumbers);
        return response;
    }

    private void validateRoleLimits(
            List<OverrideRuleEntity> rules,
            Set<Integer> requiredRoleNumbers,
            Integer cashDifference,
            Integer ticketDifference) {

        for (Integer roleNumber : requiredRoleNumbers) {
            RoleEntity role = roleRepository.findById(roleNumber).orElse(null);
            if (role == null) {
                continue;
            }

            boolean cashRuleApplies = rules.stream()
                    .anyMatch(rule -> CASH_RULE_NUMBER == rule.getRuleNumber()
                            && roleNumber.equals(rule.getOverrideRoleNumber()));
            if (cashRuleApplies && role.getOverrideCashLimit() != null
                    && cashDifference > role.getOverrideCashLimit()) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Cash difference exceeds override cash limit for role " + role.getRoleName());
            }

            boolean ticketRuleApplies = rules.stream()
                    .anyMatch(rule -> TICKET_RULE_NUMBER == rule.getRuleNumber()
                            && roleNumber.equals(rule.getOverrideRoleNumber()));
            if (ticketRuleApplies && role.getOverrideTicketLimit() != null
                    && ticketDifference > role.getOverrideTicketLimit()) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Ticket difference exceeds override ticket limit for role " + role.getRoleName());
            }
        }
    }

    private String getRoleNameOrFallback(Integer roleNumber) {
        if (roleNumber == null) {
            return "Unknown Role";
        }
        return roleRepository.findById(roleNumber)
                .map(RoleEntity::getRoleName)
                .orElse("Role " + roleNumber);
    }

    private OverrideRuleResponse toResponse(OverrideRuleEntity entity) {
        OverrideRuleResponse response = new OverrideRuleResponse();
        response.setRuleNumber(entity.getRuleNumber());
        response.setDescription(entity.getDescription());
        response.setOverrideRoleNumber(entity.getOverrideRoleNumber());
        response.setOverrideRoleName(getRoleNameOrFallback(entity.getOverrideRoleNumber()));
        return response;
    }
}
