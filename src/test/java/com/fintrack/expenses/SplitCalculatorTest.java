package com.fintrack.expenses;

import static org.junit.jupiter.api.Assertions.*;

import com.fintrack.common.BusinessValidationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SplitCalculatorTest {

    private static ParticipantRequest p(String id) { return new ParticipantRequest(id, null); }
    private static ParticipantRequest p(String id, String amt) { return new ParticipantRequest(id, new BigDecimal(amt)); }

    @Test
    void test1_equalSplitAmongThreeParticipants_sharesAddUpExactly() {
        Map<String, BigDecimal> r = SplitCalculator.calculate("alice", new BigDecimal("100.00"),
                SplitType.EQUAL, List.of(p("alice"), p("bob"), p("carol")));
        assertEquals(new BigDecimal("33.34"), r.get("alice"));
        assertEquals(new BigDecimal("33.33"), r.get("bob"));
        assertEquals(new BigDecimal("33.33"), r.get("carol"));
        assertEquals(0, new BigDecimal("100.00").compareTo(r.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add)));
    }

    @Test
    void test2_customSplitMatchingTotal_isAccepted() {
        Map<String, BigDecimal> r = SplitCalculator.calculate("alice", new BigDecimal("120.00"),
                SplitType.CUSTOM, List.of(p("alice", "20.00"), p("bob", "40.00"), p("carol", "60.00")));
        assertEquals(new BigDecimal("60.00"), r.get("carol"));
        assertEquals(3, r.size());
    }

    @Test
    void test3_customSplitNotSummingToTotal_failsValidation() {
        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () ->
                SplitCalculator.calculate("alice", new BigDecimal("120.00"), SplitType.CUSTOM,
                        List.of(p("alice", "20.00"), p("bob", "40.00"), p("carol", "50.00"))));
        assertTrue(ex.getMessage().contains("110.00"));
    }

    @Test
    void test4_singleParticipant_failsGracefully() {
        assertThrows(BusinessValidationException.class, () ->
                SplitCalculator.calculate("alice", new BigDecimal("50.00"), SplitType.EQUAL, List.of(p("alice"))));
    }

    @Test
    void test5_duplicateParticipantsAndMissingCreator_areRejected() {
        assertThrows(BusinessValidationException.class, () ->
                SplitCalculator.calculate("alice", new BigDecimal("50.00"), SplitType.EQUAL, List.of(p("bob"), p("bob"))));
        assertThrows(BusinessValidationException.class, () ->
                SplitCalculator.calculate("alice", new BigDecimal("50.00"), SplitType.EQUAL, List.of(p("bob"), p("carol"))));
    }
}
