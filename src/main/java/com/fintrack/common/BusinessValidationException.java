package com.fintrack.common;

/** Thrown when a business rule is violated (e.g. custom split does not sum to total). Maps to HTTP 400. */
public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) { super(message); }
}
