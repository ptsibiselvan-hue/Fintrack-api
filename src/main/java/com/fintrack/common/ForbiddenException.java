package com.fintrack.common;

/** Thrown when a user tries to access a resource they do not own. Maps to HTTP 403. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}
