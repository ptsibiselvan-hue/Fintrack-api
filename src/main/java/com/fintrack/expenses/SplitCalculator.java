package com.fintrack.expenses;

import com.fintrack.common.BusinessValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Pure business logic that turns a total and participants into per-person shares.
 * No Spring, no database - trivially unit-testable.
 */
public final class SplitCalculator {

    private SplitCalculator() { }

    /**
     * Calculates each participant's share.
     *
     * <p>EQUAL splits distribute leftover cents (e.g. 100.00 / 3 = 33.34, 33.33, 33.33) so the shares
     * always add up exactly to the total. CUSTOM splits must add up exactly to the total.
     *
     * @param creatorId    the payer; must be one of the participants
     * @param total        total amount (positive, max 2 decimals)
     * @param type         EQUAL or CUSTOM
     * @param participants participants in request order
     * @return ordered map participantId -> share
     * @throws BusinessValidationException when any rule is violated
     */
    public static Map<String, BigDecimal> calculate(String creatorId, BigDecimal total, SplitType type,
                                                    List<ParticipantRequest> participants) {
        if (total == null || total.signum() <= 0 || total.scale() > 2) {
            throw new BusinessValidationException("Total must be positive with at most 2 decimal places");
        }
        if (participants == null || participants.size() < 2) {
            throw new BusinessValidationException("An expense needs at least 2 participants");
        }
        Set<String> ids = participants.stream().map(ParticipantRequest::userId).collect(Collectors.toSet());
        if (ids.size() != participants.size()) {
            throw new BusinessValidationException("Participants must be unique");
        }
        if (!ids.contains(creatorId)) {
            throw new BusinessValidationException("The creator must be one of the participants");
        }
        return type == SplitType.EQUAL ? equalSplit(total, participants) : customSplit(total, participants);
    }

    private static Map<String, BigDecimal> equalSplit(BigDecimal total, List<ParticipantRequest> participants) {
        int n = participants.size();
        long totalCents = total.movePointRight(2).longValueExact();
        long base = totalCents / n;
        long remainder = totalCents % n;
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            long cents = base + (i < remainder ? 1 : 0);
            result.put(participants.get(i).userId(), BigDecimal.valueOf(cents, 2));
        }
        return result;
    }

    private static Map<String, BigDecimal> customSplit(BigDecimal total, List<ParticipantRequest> participants) {
        BigDecimal sum = BigDecimal.ZERO;
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (ParticipantRequest p : participants) {
            BigDecimal amt = p.amount();
            if (amt == null || amt.signum() <= 0 || amt.scale() > 2) {
                throw new BusinessValidationException(
                        "Custom amount for " + p.userId() + " must be positive with at most 2 decimals");
            }
            sum = sum.add(amt);
            result.put(p.userId(), amt.setScale(2, RoundingMode.UNNECESSARY));
        }
        if (sum.compareTo(total) != 0) {
            throw new BusinessValidationException(
                    "Custom amounts add up to " + sum.toPlainString() + " but total is " + total.toPlainString());
        }
        return result;
    }
}
