package com.CasinoCtC.CCtCAPI.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransactionRequest {

    // ✅ Header Fields

    @NotBlank(message =
            "Business Date is required")
    private String businessDate;

    @NotBlank(message =
            "Collection Date is required")
    private String collectionDate;

    @NotBlank(message =
            "Box Number is required")
    private String boxNumber;

    @NotNull(message =
            "Location ID is required")
    @Positive(message =
            "Location ID must be positive")
    private Integer locationID;

    @NotNull(message =
            "User ID is required")
    @Positive(message =
            "User ID must be positive")
    private Integer userID;

    // ✅ Currency Rows

    @NotEmpty(message =
            "At least one currency row is required")
    @Valid
    private List<TransactionCurrencyRequest>
            currency;

    // ✅ Ticket Rows
    // Optional → no @NotEmpty

    @Valid
    private List<TransactionTicketRequest>
            tickets;

    // ✅ Getters and Setters

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(
            String businessDate) {

        this.businessDate = businessDate;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(
            String collectionDate) {

        this.collectionDate = collectionDate;
    }

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(
            String boxNumber) {

        this.boxNumber = boxNumber;
    }

    public Integer getLocationID() {
        return locationID;
    }

    public void setLocationID(
            Integer locationID) {

        this.locationID = locationID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(
            Integer userID) {

        this.userID = userID;
    }

    public List<TransactionCurrencyRequest>
    getCurrency() {

        return currency;
    }

    public void setCurrency(
            List<TransactionCurrencyRequest>
                    currency) {

        this.currency = currency;
    }

    public List<TransactionTicketRequest>
    getTickets() {

        return tickets;
    }

    public void setTickets(
            List<TransactionTicketRequest>
                    tickets) {

        this.tickets = tickets;
    }
}