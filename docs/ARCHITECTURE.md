# Architecture

```
HTTP request (X-User-Id)
      |
      v
 Controller  -- parses/validates input, identifies caller, maps to response DTO
      |
      v
  Service    -- business rules + authorisation (SplitCalculator / BalanceCalculator are pure logic)
      |
      v
 Repository  -- Spring Data JPA, parameterised queries only
      |
      v
 Model/Entity (Hibernate) --> H2 (dev) / PostgreSQL (prod)
```

Flow for `POST /api/expenses`: ExpenseController -> ExpenseService -> SplitCalculator (validate + split)
-> SharedExpenseRepository (save) -> TransactionService (records the payer's DEBIT) -> TransactionRepository.

1. The `transactions` module is the ledger foundation; `expenses` builds on it by calling `TransactionService`
   when an expense is created, never touching `TransactionRepository` directly.
2. Dependencies point one way only: controller -> service -> repository -> model. Controllers never hold SQL/entities in responses.
3. All money is `BigDecimal` with scale 2; floats are never used, so there are no rounding drifts.
4. Equal splits distribute leftover cents (100.00 / 3 = 33.34 + 33.33 + 33.33) so shares always sum to the total.
5. Custom splits must sum exactly to the total, otherwise HTTP 400.
6. Authorisation is enforced in the service layer: users read/delete only their own transactions and balances (HTTP 403).
7. A central `@RestControllerAdvice` maps specific exceptions to 400/401/403/404 and hides internals on 500.
8. Structured `event=... key=value` logging gives an audit trail without logging sensitive data.
9. Fintech fit: strict layering keeps business rules testable and auditable, and the ORM removes SQL-injection risk.
10. Key decision: pure calculators are framework-free, so the riskiest maths is unit-tested without Spring.
11. Key decision: balances are derived from stored shares (single source of truth) rather than a cached balance column.
12. Known trade-off: `X-User-Id` is a placeholder for real token authentication (see README).
