package com.portfolio.finance.exception;

public class InsufficientBalanceException extends ApiException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
