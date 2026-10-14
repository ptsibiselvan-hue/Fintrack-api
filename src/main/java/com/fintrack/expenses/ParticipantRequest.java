package com.fintrack.expenses;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

/** A participant in the request. {@code amount} is required only for CUSTOM splits. */
public record ParticipantRequest(@NotBlank String userId, BigDecimal amount) { }
