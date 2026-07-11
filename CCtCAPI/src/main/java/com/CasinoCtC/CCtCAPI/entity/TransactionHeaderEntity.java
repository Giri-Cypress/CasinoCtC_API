package com.CasinoCtC.CCtCAPI.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction_header", schema = "gsi")
public class TransactionHeaderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trans_number")
    private Integer transNumber;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;

    @Column(name = "box_number", nullable = false, length = 50)
    private String boxNumber;

    @Column(name = "workstation_id", nullable = false, length = 50)
    private String workstationId;

    @Column(name = "process_user", nullable = false, length = 50)
    private String processUser;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "completed_by", length = 50)
    private String completedBy;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @Column(name = "cancelled_by", length = 50)
    private String cancelledBy;

    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    @Column(name = "last_update_by", length = 50)
    private String lastUpdateBy;

    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;
}