package com.fintrack.expenses;

import java.math.BigDecimal;

/** A single obligation: {@code debtor} owes {@code creditor} {@code amount}. */
public record Debt(String debtor, String creditor, BigDecimal amount) { }
