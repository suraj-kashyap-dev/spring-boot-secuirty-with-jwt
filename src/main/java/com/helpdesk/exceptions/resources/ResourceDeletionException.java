package com.helpdesk.exceptions.resources;

public class ResourceDeletionException extends RuntimeException {
    public ResourceDeletionException(String message) {
        super(message);
    }
}
