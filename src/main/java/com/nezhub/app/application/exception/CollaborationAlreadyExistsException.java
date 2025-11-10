package com.nezhub.app.application.exception;

public class CollaborationAlreadyExistsException extends RuntimeException {
    public CollaborationAlreadyExistsException(String message) {
        super(message);
    }
}
