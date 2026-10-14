## Test Coverage Review

### Existing Coverage

`TransactionApiTest.java` currently verifies:
- A user cannot read another user's transaction.
- Negative transaction amounts are rejected.
- Deleting one user's transactions does not delete another user's transactions.

### Remaining Test Gaps

1. No visible test verifies behavior when the `X-User-Id` header is missing or blank.
2. No visible test verifies malformed request bodies or invalid transaction types.
3. No visible test verifies the maximum supported amount and description length boundaries.
4. The existing tests exercise ownership using a caller-supplied header, not verified authentication.

These are gaps in the inspected test file; other tests or application layers may address some of them.

### Overall Human Assessment

The transaction module already uses `BigDecimal`, request validation through `@Valid`, user-scoped repository methods, and an ownership check when retrieving an individual transaction. The most significant production security concern is that the controller trusts `X-User-Id` rather than deriving identity from a verified authentication context. Validation and test improvements should be prioritized without misrepresenting existing functionality.

## Expense Split Calculator Review

### Existing Strengths

- Uses `BigDecimal` for monetary calculations.
- Distributes leftover cents in equal splits so the shares add up to the total.
- Rejects custom splits when participant amounts do not match the total.
- Checks for at least two participants, duplicate participant IDs, and inclusion of the creator.
- Rejects non-positive totals and custom amounts with more than two decimal places.

### Findings Requiring Attention

1. **Potential overflow for large equal splits:** `equalSplit()` converts the total to a `long` number of cents using `longValueExact()`. An amount outside the supported range throws `ArithmeticException`. Define and validate an appropriate maximum expense amount, or use an arithmetic approach that supports the required range.

2. **Missing explicit split-type validation:** `calculate()` uses a conditional expression that treats every value other than `SplitType.EQUAL` as a custom split. A null split type should be rejected explicitly with a clear validation exception.

3. **Participant ID validation requires verification:** The calculator checks for duplicate IDs and creator membership but does not explicitly reject null or blank participant IDs. Verify whether `ParticipantRequest` or request validation already handles these cases.

### Human Judgment

The calculator has useful business-rule checks and correctly handles ordinary equal and custom splits. The findings above should be assessed against the request DTOs, API validation, and expected business limits before deciding which changes are required. Existing behavior should remain covered by tests during remediation.