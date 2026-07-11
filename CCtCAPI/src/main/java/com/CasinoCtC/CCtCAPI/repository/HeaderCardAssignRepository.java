package com.CasinoCtC.CCtCAPI.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.HeaderCardAssignEntity;

@Repository
public interface HeaderCardAssignRepository extends JpaRepository<HeaderCardAssignEntity, Long> {

    Optional<HeaderCardAssignEntity> findByHeaderCardId(String headerCardId);

    Optional<HeaderCardAssignEntity> findFirstByHeaderCardIdOrderByHcAssignNumberDesc(String headerCardId);

    Optional<HeaderCardAssignEntity> findFirstByHeaderCardIdAndStatusOrderByHcAssignNumberDesc(
            String headerCardId,
            Integer status
    );

    Optional<HeaderCardAssignEntity> findFirstByHeaderCardIdAndStatusLessThanOrderByHcAssignNumberDesc(
            String headerCardId,
            Integer status
    );

    boolean existsByStatus(Integer status);
}
