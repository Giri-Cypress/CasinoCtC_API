package com.CasinoCtC.CCtCAPI.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions", schema = "gsi")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trans_number")
    private Long transNumber;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;

    @Column(name = "box_number", nullable = false, length = 50)
    private String boxNumber;

    @Column(name = "location_number", nullable = false)
    private Integer locationNumber;

    @Column(name = "user_number", nullable = false)
    private Integer userNumber;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "declared_currency_total")
    private Long declaredCurrencyTotal;

    @Column(name = "declared_ticket_count")
    private Integer declaredTicketCount;

    @Column(name = "total_currency", nullable = false)
    private Long totalCurrency;

    @Column(name = "ticket_count", nullable = false)
    private Integer ticketCount;

    @Column(name = "total_tkt_amount", nullable = false)
    private Long totalTktAmount;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "supervisor_override")
    private Integer supervisorOverride;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "trans_number_edited")
    private Long transNumberEdited;

    @Column(name = "header_card", length = 64)
    private String headerCard;

    @Column(name = "header_card_file", length = 128)
    private String headerCardFile;

    public Long getTransNumber() {
        return transNumber;
    }

    public void setTransNumber(Long transNumber) {
        this.transNumber = transNumber;
    }

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(LocalDate businessDate) {
        this.businessDate = businessDate;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(LocalDate collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }

    public Integer getLocationNumber() {
        return locationNumber;
    }

    public void setLocationNumber(Integer locationNumber) {
        this.locationNumber = locationNumber;
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDeclaredCurrencyTotal() {
        return declaredCurrencyTotal;
    }

    public void setDeclaredCurrencyTotal(Long declaredCurrencyTotal) {
        this.declaredCurrencyTotal = declaredCurrencyTotal;
    }

    public Integer getDeclaredTicketCount() {
        return declaredTicketCount;
    }

    public void setDeclaredTicketCount(Integer declaredTicketCount) {
        this.declaredTicketCount = declaredTicketCount;
    }

    public Long getTotalCurrency() {
        return totalCurrency;
    }

    public void setTotalCurrency(Long totalCurrency) {
        this.totalCurrency = totalCurrency;
    }

    public Integer getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Integer ticketCount) {
        this.ticketCount = ticketCount;
    }

    public Long getTotalTktAmount() {
        return totalTktAmount;
    }

    public void setTotalTktAmount(Long totalTktAmount) {
        this.totalTktAmount = totalTktAmount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getSupervisorOverride() {
        return supervisorOverride;
    }

    public void setSupervisorOverride(Integer supervisorOverride) {
        this.supervisorOverride = supervisorOverride;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getTransNumberEdited() {
        return transNumberEdited;
    }

    public void setTransNumberEdited(Long transNumberEdited) {
        this.transNumberEdited = transNumberEdited;
    }
    public String getHeaderCard() {
        return headerCard;
    }

    public void setHeaderCard(String headerCard) {
        this.headerCard = headerCard;
    }

    public String getHeaderCardFile() {
        return headerCardFile;
    }

    public void setHeaderCardFile(String headerCardFile) {
        this.headerCardFile = headerCardFile;
    }

}