package com.fintrack.expenses;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pure logic that nets debts between a user and every counterparty. */
public final class BalanceCalculator {

    private BalanceCalculator() { }

    /**
     * Computes the user's balance with each counterparty.
     * Example: A owes B 30 and B owes A 10 gives, for A, net -20 (A owes B 20).
     *
     * @param userId the user whose view is requested
     * @param debts  all debts; entries not involving the user are ignored
     * @return one entry per counterparty, sorted by first appearance
     */
    public static List<BalanceEntry> compute(String userId, List<Debt> debts) {
        Map<String, BigDecimal[]> byPerson = new LinkedHashMap<>(); // [owedToYou, youOwe]
        for (Debt d : debts) {
            if (d.creditor().equals(userId) && !d.debtor().equals(userId)) {
                byPerson.computeIfAbsent(d.debtor(), k -> zeros())[0] =
                        byPerson.get(d.debtor())[0].add(d.amount());
            } else if (d.debtor().equals(userId) && !d.creditor().equals(userId)) {
                byPerson.computeIfAbsent(d.creditor(), k -> zeros())[1] =
                        byPerson.get(d.creditor())[1].add(d.amount());
            }
        }
        List<BalanceEntry> result = new ArrayList<>();
        byPerson.forEach((person, v) -> {
            BigDecimal net = v[0].subtract(v[1]);
            String dir = net.signum() > 0 ? "THEY_OWE_YOU" : net.signum() < 0 ? "YOU_OWE_THEM" : "SETTLED";
            result.add(new BalanceEntry(person, v[0], v[1], net, dir));
        });
        return result;
    }

    private static BigDecimal[] zeros() {
        return new BigDecimal[] {BigDecimal.ZERO.setScale(2), BigDecimal.ZERO.setScale(2)};
    }
}
