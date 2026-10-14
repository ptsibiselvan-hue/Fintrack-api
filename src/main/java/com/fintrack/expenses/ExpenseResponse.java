package com.fintrack.expenses;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** API view of a shared expense. */
public record ExpenseResponse(Long id, String creatorId, String description, BigDecimal totalAmount,
                              SplitType splitType, Instant createdAt, List<Share> participants) {

    public record Share(String userId, BigDecimal shareAmount) { }

    static ExpenseResponse from(SharedExpense e) {
        return new ExpenseResponse(e.getId(), e.getCreatorId(), e.getDescription(), e.getTotalAmount(),
                e.getSplitType(), e.getCreatedAt(),
                e.getShares().stream().map(s -> new Share(s.getParticipantId(), s.getShareAmount())).toList());
    }
}
