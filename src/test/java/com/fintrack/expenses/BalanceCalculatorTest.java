package com.fintrack.expenses;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class BalanceCalculatorTest {

    private static Debt d(String debtor, String creditor, String amt) {
        return new Debt(debtor, creditor, new BigDecimal(amt));
    }

    @Test
    void test6_netBalanceBetweenTwoUsersAcrossMultipleExpenses() {
        // A owes B 20 + 10 = 30 ; B owes A 10  => net: A owes B 20
        List<Debt> debts = List.of(d("A", "B", "20.00"), d("A", "B", "10.00"), d("B", "A", "10.00"));

        BalanceEntry forA = BalanceCalculator.compute("A", debts).get(0);
        assertEquals("B", forA.counterparty());
        assertEquals(new BigDecimal("20.00"), forA.net().negate());
        assertEquals("YOU_OWE_THEM", forA.direction());

        BalanceEntry forB = BalanceCalculator.compute("B", debts).get(0);
        assertEquals(new BigDecimal("20.00"), forB.net());
        assertEquals("THEY_OWE_YOU", forB.direction());
    }

    @Test
    void test7_unrelatedDebtsIgnored_andSettledDetected() {
        List<Debt> debts = List.of(d("A", "B", "10.00"), d("B", "A", "10.00"), d("C", "D", "99.00"));
        List<BalanceEntry> forA = BalanceCalculator.compute("A", debts);
        assertEquals(1, forA.size());
        assertEquals("SETTLED", forA.get(0).direction());
    }
}
