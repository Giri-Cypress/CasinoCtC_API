package com.CasinoCtC.CCtCAPI.exception;

public class BusinessValidationException
        extends RuntimeException {

    public BusinessValidationException(
            String message) {

        super(message);
    }
}