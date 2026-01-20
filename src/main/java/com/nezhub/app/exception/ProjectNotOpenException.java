package com.nezhub.app.exception;

public class ProjectNotOpenException extends RuntimeException {
    public ProjectNotOpenException(String message) {
        super(message);
    }
}
