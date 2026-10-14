package com.fintrack.expenses;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/** Request body for creating a shared expense. */
public record CreateExpenseRequest(
        @NotBlank @Size(max = 255) String description,
        @NotNull @DecimalMin(value = "0.01", message = "must be at least 0.01")
        @Digits(integer = 17, fraction = 2, message = "must have at most 2 decimal places") BigDecimal totalAmount,
        @NotNull SplitType splitType,
        @NotEmpty @Valid List<ParticipantRequest> participants) { }
