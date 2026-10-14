package com.fintrack.expenses;

import java.math.BigDecimal;

/**
 * Net position of the requesting user against one counterparty.
 *
 * @param owedToYou  total the counterparty owes the user
 * @param youOwe     total the user owes the counterparty
 * @param net        owedToYou - youOwe (positive: they owe you)
 * @param direction  THEY_OWE_YOU, YOU_OWE_THEM or SETTLED
 */
public record BalanceEntry(String counterparty, BigDecimal owedToYou, BigDecimal youOwe,
                           BigDecimal net, String direction) { }
