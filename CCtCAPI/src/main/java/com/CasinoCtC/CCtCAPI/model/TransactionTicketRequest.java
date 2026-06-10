package com.CasinoCtC.CCtCAPI.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class TransactionTicketRequest {

    @NotBlank(message =
            "Ticket ID is required")
    @Size(
            max = 100,
            message =
                    "Ticket ID cannot exceed 100 characters"
    )
    private String ticketID;

    @NotNull(message =
            "Amount is required")
    @PositiveOrZero(message =
            "Amount cannot be negative")
    private Long amount;

    // ✅ Getters and Setters

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(
            String ticketID) {

        this.ticketID = ticketID;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(
            Long amount) {

        this.amount = amount;
    }
}