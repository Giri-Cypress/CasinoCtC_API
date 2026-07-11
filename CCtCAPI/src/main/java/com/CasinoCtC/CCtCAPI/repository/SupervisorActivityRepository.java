package com.CasinoCtC.CCtCAPI.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.entity.SupervisorActivityEntity;

@Repository
public interface SupervisorActivityRepository extends JpaRepository<SupervisorActivityEntity, Long> {

    @Modifying
    @Query("""
            update SupervisorActivityEntity a
               set a.transNumber = :transNumber
             where a.activityNumber in :activityNumbers
               and a.transNumber is null
            """)
    int attachToTransaction(
            @Param("activityNumbers") Collection<Long> activityNumbers,
            @Param("transNumber") Long transNumber);
}
