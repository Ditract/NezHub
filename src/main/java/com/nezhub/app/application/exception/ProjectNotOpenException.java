package com.nezhub.app.application.exception;

public class ProjectNotOpenException extends RuntimeException {
    public ProjectNotOpenException(String message) {
        super(message);
    }
}
