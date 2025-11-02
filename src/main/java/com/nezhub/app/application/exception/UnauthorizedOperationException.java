package com.nezhub.app.application.exception;

public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
