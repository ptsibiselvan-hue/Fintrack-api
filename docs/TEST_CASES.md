# Test Case Documentation

Run with `mvn test`. 12 test methods.

| # | Class | Scenario | Expected |
|---|---|---|---|
| 1 | SplitCalculatorTest | Equal split of 100.00 among 3 | 33.34 / 33.33 / 33.33, sums to 100.00 |
| 2 | SplitCalculatorTest | Custom split 20+40+60 = 120 | Accepted |
| 3 | SplitCalculatorTest | Custom split 20+40+50 vs total 120 | BusinessValidationException (400) |
| 4 | SplitCalculatorTest | Only 1 participant | Rejected gracefully |
| 5 | SplitCalculatorTest | Duplicate participants; creator missing | Rejected |
| 6 | BalanceCalculatorTest | A owes B 30, B owes A 10 | Net: A owes B 20 (and B is owed 20) |
| 7 | BalanceCalculatorTest | Equal opposite debts; unrelated debts | SETTLED; unrelated ignored |
| 8 | ExpenseApiTest | Create equal and custom expenses, read balances | Net 35.00 THEY_OWE_YOU |
| 9 | ExpenseApiTest | Read another user's balances; no header | 403; 401 |
| 10 | ExpenseApiTest | Custom split not matching total via API | 400 |
| 11 | TransactionApiTest | Read another user's transaction | Owner 200, other user 403 |
| 12 | TransactionApiTest | Negative amount; delete-all scope | 400; only caller's rows deleted |
