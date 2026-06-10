package com.CasinoCtC.CCtCAPI.dto;

import lombok.Data;

import java.util.List;

@Data
public class TransactionRequest {

    private HeaderDTO header;

    private List<CurrencyDTO> cash;

    private List<TicketDTO> tickets;

}