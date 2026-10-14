package com.fintrack.common;

import java.time.Instant;

/** Uniform error body returned by the API. Never contains stack traces or internal details. */
public record ErrorResponse(Instant timestamp, int status, String error, String message) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(Instant.now(), status, error, message);
    }
}
