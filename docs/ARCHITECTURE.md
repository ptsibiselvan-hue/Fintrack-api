# FinTrack API Architecture

## 1. Overview

FinTrack is a Spring Boot REST API for managing transactions, shared expenses, and user balances.

## 2. Technology Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Jakarta Bean Validation
- Maven
- JUnit and Spring Boot testing
- Relational database configured through application properties

## 3. Application Layers

### Controller Layer

Exposes REST endpoints, receives HTTP requests, applies request validation, and returns API responses.

### Service Layer

Contains transaction and expense business logic, including ownership checks and expense creation workflows.

### Repository Layer

Uses Spring Data JPA to access and manage persistent transaction and expense data.

### Domain and DTO Layer

Contains entities, request records, response records, transaction types, expense types, participants, and balance models.

### Calculation Layer

`SplitCalculator` calculates equal and custom participant shares. `BalanceCalculator` computes net balances between users.

## 4. Transaction Flow

An HTTP request reaches `TransactionController`, is validated, and is passed to `TransactionService`. The service uses `TransactionRepository` to access transaction data and returns the result through a response model.

## 5. Expense Flow

An expense request reaches the expense controller, is validated, and is processed by the expense service. The split calculator determines participant shares, and the application persists the expense and related data.

## 6. Balance Calculation

The balance calculator processes debts and derives net amounts and balance directions for users.

## 7. Validation and Error Handling

Request validation uses Jakarta Bean Validation. Application exceptions are translated into HTTP responses through the common API exception handler.

## 8. Persistence

Spring Data JPA manages entity persistence. Database connection details are configured externally through application properties and environment variables where applicable.

## 9. Security Boundary

The current controller uses the `X-User-Id` header as a temporary identity mechanism. It must not be considered verified authentication. Production deployment requires authenticated identity and authorization enforcement.

## 10. Testing

JUnit and Spring Boot tests cover transaction APIs, expense APIs, split calculations, and balance calculations.

## 11. Known Limitations

- Production authentication is not established by the user ID header.
- Large equal-split totals may exceed the current long-based cent conversion range.
- Further boundary and security tests are recommended.

## 12. Deployment Considerations

Before production deployment, configure secure authentication, database credentials, logging, monitoring, and environment-specific settings. Run the complete test suite after final changes.
