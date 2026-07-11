package com.CasinoCtC.CCtCAPI.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CasinoCtC.CCtCAPI.entity.SupervisorActivityEntity;
import com.CasinoCtC.CCtCAPI.repository.SupervisorActivityRepository;

@Service
public class SupervisorActivityService {

    private final SupervisorActivityRepository supervisorActivityRepository;

    public SupervisorActivityService(SupervisorActivityRepository supervisorActivityRepository) {
        this.supervisorActivityRepository = supervisorActivityRepository;
    }

    @Transactional
    public List<Long> recordOverrideActivities(
            Integer processUser,
            Integer supervisorUser,
            Collection<Integer> differenceAmounts) {

        List<Long> activityNumbers = new ArrayList<>();
        if (processUser == null || supervisorUser == null || differenceAmounts == null) {
            return activityNumbers;
        }

        for (Integer differenceAmount : differenceAmounts) {
            if (differenceAmount == null) {
                continue;
            }

            SupervisorActivityEntity entity = new SupervisorActivityEntity();
            entity.setProcessUser(processUser);
            entity.setSupervisorUser(supervisorUser);
            entity.setDifferenceAmount(Math.abs(differenceAmount));
            entity.setCreatedAt(LocalDateTime.now().withNano(0));

            SupervisorActivityEntity saved = supervisorActivityRepository.save(entity);
            activityNumbers.add(saved.getActivityNumber());
        }

        return activityNumbers;
    }

    @Transactional
    public void attachToTransaction(Collection<Long> activityNumbers, Long transNumber) {
        if (activityNumbers == null || activityNumbers.isEmpty() || transNumber == null) {
            return;
        }
        supervisorActivityRepository.attachToTransaction(activityNumbers, transNumber);
    }
}
