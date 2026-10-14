package com.fintrack.transactions;

import java.math.BigDecimal;
import java.time.Instant;

/** API view of a transaction. */
public record TransactionResponse(Long id, String userId, BigDecimal amount, TransactionType type,
                                  String description, Instant createdAt) {
    static TransactionResponse from(Transaction t) {
        return new TransactionResponse(t.getId(), t.getUserId(), t.getAmount(), t.getType(),
                t.getDescription(), t.getCreatedAt());
    }
}
