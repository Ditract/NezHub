package com.nezhub.app.exception;

public class CollaborationAlreadyExistsException extends RuntimeException {
    public CollaborationAlreadyExistsException(String message) {
        super(message);
    }
}
