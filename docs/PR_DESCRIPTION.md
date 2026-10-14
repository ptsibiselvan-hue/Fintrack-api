# Pull Request: Improve FinTrack Transaction and Expense Splitting

## 1. Summary

This pull request covers the review, testing, and documentation of the FinTrack API transaction management and expense-splitting functionality. It records the current implementation, identifies potential security and validation risks, and documents the verification required before production use.

## 2. AI Assistance Disclosure

GitHub Copilot was used as an AI-assisted development tool during the project.

AI-generated suggestions were reviewed against the project requirements, existing implementation, validation rules, and test results. The developer remains responsible for reviewing generated code, correcting issues, and verifying the final behavior.

The exact prompts and post-generation corrections must be documented in `PROMPTS.md` using the actual Copilot interaction history.

## 3. Changes

### Transaction Module

- Reviewed the `Transaction` entity, service, controller, repository, request validation, and API tests.
- Verified the use of `BigDecimal` for monetary values.
- Reviewed transaction ownership checks and user-scoped repository operations.
- Documented the risks associated with trusting the `X-User-Id` request header.
- Identified areas for additional validation and test coverage.

### Expense Splitting Module

- Reviewed equal and custom expense-splitting calculations.
- Verified that equal-split rounding distributes leftover cents.
- Reviewed custom-split total validation.
- Checked participant uniqueness and creator membership rules.
- Documented potential overflow for very large equal-split totals and missing explicit split-type validation.

### Documentation

- `docs/REVIEW.md` — code review findings, existing strengths, and test gaps.
- `docs/PROMPTS.md` — AI prompt history and post-generation corrections.
- `docs/PR_DESCRIPTION.md` — pull request summary, testing, risks, and review checklist.

## 4. Testing

The verified Maven test run completed successfully.

**Latest observed test results:**

- Tests run: 12
- Failures: 0
- Errors: 0
- Skipped: 0
- Build status: `BUILD SUCCESS`

The test suite includes transaction API tests, expense API tests, equal and custom split calculator tests, and balance calculator tests.

These results describe the observed test run. Rerun the full test suite after any subsequent code changes.

## 5. Security Considerations

### User Identity

The transaction and expense controllers use the `X-User-Id` request header as a temporary identity mechanism.

A client can supply its own header value. Therefore, this mechanism must not be treated as verified authentication in production.

**Recommended improvement:** Integrate Spring Security with verified authentication, such as JWT-based authentication, and derive the caller's identity from the authenticated security context.

### Authorization

The transaction service checks ownership when retrieving an individual transaction. User-scoped repository operations are used for listing and deleting transactions.

These protections depend on the supplied user identity being trustworthy.

### Input Validation

The transaction request applies validation for the amount, transaction type, and description. The expense-splitting implementation also validates important business rules.

Additional tests should verify missing or blank user IDs, invalid split types, boundary amounts, and malformed requests where applicable.

## 6. Known Risks and Limitations

1. **Authentication:** `X-User-Id` is not a substitute for verified production authentication.
2. **Large expense amounts:** Equal splitting converts the total to a `long` number of cents. Extremely large values may cause an `ArithmeticException`.
3. **Split-type validation:** `SplitCalculator.calculate()` should explicitly reject a null split type rather than treating every non-`EQUAL` value as a custom split.
4. **Participant validation:** Direct calls to the calculator should not rely solely on request-level validation for participant IDs.
5. **Test coverage:** Additional tests are recommended for missing headers, malformed requests, boundary values, and split-type validation.

These risks should be reviewed against the final implementation before release.

## 7. Reviewer Checklist

Complete each item only after verifying it.

- [x] Review the final code changes.
- [ ] Verify request validation and ownership checks.
- [ ] Verify expense-splitting calculations and rounding.
- [x] Run the full Maven test suite — 12 tests, 0 failures, 0 errors, 0 skipped.
- [ ] Confirm that no secrets or credentials are committed.
- [ ] Confirm that the documentation matches the actual implementation.
- [ ] Verify that the original, unmodified Copilot-generated Transaction code is preserved as required by the assessment.
- [ ] Verify that the actual prompt history and post-generation corrections are documented.
- [ ] Verify that the required screenshots and final assessment document are complete.

**Checklist note:** The code review and test execution have been performed during this workflow. Recheck the remaining items before submission and update their status based on actual verification.

## 8. Peer Review Comments

Record three genuine peer-review comments after obtaining feedback. Do not invent reviewer identities, approvals, or comments.

1. **Reviewer 1:** Pending actual feedback.
2. **Reviewer 2:** Pending actual feedback.
3. **Reviewer 3:** Pending actual feedback.

For each reviewer, record the actual feedback and any corresponding changes made.

## 9. Verification Before Merge

- Run `mvn clean test` after the final code changes.
- Review the Git diff and confirm that only intended files are included.
- Check that no credentials, tokens, or private configuration values are committed.
- Verify the original Copilot-generated code and actual prompt history.
- Confirm that all required assessment documents and evidence are present.
- Address critical security issues before production deployment.

## 10. Final Decision

**Status:** Pending final assessment verification and genuine peer review.

The successful test run confirms that the current test suite passes. It does not, by itself, establish production readiness or verify every assessment requirement.
