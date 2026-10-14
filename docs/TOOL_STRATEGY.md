# Tool Strategy — FinTrack API

## 1. GitHub Copilot Chat

## 1. GitHub Copilot Chat

**Purpose:** Understand existing Java code, explore implementation choices, and explain how application components work together.

**Actual usage:** Asked GitHub Copilot to explain the purpose of `Transaction.java` without modifying files. Copilot inspected `Transaction.java`, `TransactionService.java`, `TransactionController.java`, and `TransactionType.java`.

**Observed result:** Copilot explained that `Transaction.java` is a JPA entity representing a persisted financial transaction, including its owner, amount, type, description, and creation timestamp.

**Human decision:** Reviewed the explanation against the source code. No files were modified during this interaction.

**Evidence:** Retain a genuine screenshot of the Copilot prompt and response, if available.

## 2. GitHub Copilot Code Completion

**Purpose:** Assist with routine Java implementation and repetitive code patterns.

**Human decision:** Inspect generated code for correctness, readability, validation, and consistency with project conventions.

## 3. GitHub Copilot Inline Chat

**Purpose:** Request focused explanations or changes to a selected code section.

**Human decision:** Keep changes limited to the requested scope and review the resulting diff.

## 4. GitHub Copilot Agent Mode

**Purpose:** Explore multi-file implementation tasks when available and appropriate.

**Human decision:** Review every changed file and avoid accepting broad changes without understanding their effects.

**Evidence requirement:** Record only features actually used. If Agent Mode was not used, mark this entry as planned rather than completed.

## 5. VS Code

**Purpose:** Navigate the project, inspect Java source files, edit documentation, and review changes.

**Human decision:** Verify that the correct project folder and files are being edited.

## 6. Maven

**Purpose:** Build the Java project and run automated tests.

**Observed result:** `mvn clean test` completed successfully with 12 tests, 0 failures, 0 errors, and 0 skipped.

## 7. Git and GitHub

**Purpose:** Track changes, create meaningful commits, and publish the project to the repository.

**Human decision:** Review staged files, use clear commit messages, and verify the repository contents after pushing.

## 8. Tool Selection and Verification

Tools were selected according to the task:
- Copilot for code assistance.
- VS Code for source inspection and editing.
- Maven for build and test verification.
- Git for version control.
- GitHub for remote repository collaboration.

AI-generated suggestions were not treated as automatically correct. Human review and automated tests were used to evaluate the work.

## 9. Limitations

1. AI-generated code can contain defects and requires human review.
2. Passing automated tests does not prove the absence of security vulnerabilities.
3. AI suggestions may not reflect every project requirement or production constraint.

## 10. Evidence

Add genuine screenshots showing the tools and workflows actually used. Do not claim that an unused feature was used, and do not fabricate evidence.
