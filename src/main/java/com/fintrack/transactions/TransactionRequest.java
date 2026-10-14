package com.fintrack.transactions;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** Request body for creating a transaction. */
public record TransactionRequest(
        @NotNull @DecimalMin(value = "0.01", message = "must be at least 0.01")
        @Digits(integer = 17, fraction = 2, message = "must have at most 2 decimal places") BigDecimal amount,
        @NotNull TransactionType type,
        @NotBlank @Size(max = 255) String description) { }
