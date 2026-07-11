package com.CasinoCtC.CCtCAPI.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "supervisor_activity", schema = "gsi")
public class SupervisorActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_number")
    private Long activityNumber;

    @Column(name = "process_user", nullable = false)
    private Integer processUser;

    @Column(name = "supervisor_user", nullable = false)
    private Integer supervisorUser;

    @Column(name = "difference_amount", nullable = false)
    private Integer differenceAmount;

    @Column(name = "trans_number")
    private Long transNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Long getActivityNumber() { return activityNumber; }
    public void setActivityNumber(Long activityNumber) { this.activityNumber = activityNumber; }

    public Integer getProcessUser() { return processUser; }
    public void setProcessUser(Integer processUser) { this.processUser = processUser; }

    public Integer getSupervisorUser() { return supervisorUser; }
    public void setSupervisorUser(Integer supervisorUser) { this.supervisorUser = supervisorUser; }

    public Integer getDifferenceAmount() { return differenceAmount; }
    public void setDifferenceAmount(Integer differenceAmount) { this.differenceAmount = differenceAmount; }

    public Long getTransNumber() { return transNumber; }
    public void setTransNumber(Long transNumber) { this.transNumber = transNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
