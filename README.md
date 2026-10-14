# FinTrack API

Personal-finance API with a Transaction module and an Expense Splitting feature.

## Technology stack (declared)
| Concern | Choice |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 (Spring Web) |
| ORM | Spring Data JPA / Hibernate (no raw JDBC) |
| Database | H2 in-memory for dev/test; set `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` for PostgreSQL in production |
| Validation | Jakarta Bean Validation |
| Tests | JUnit 5 + Spring Boot Test + MockMvc |
| Build | Maven |

## Run
```bash
mvn spring-boot:run        # starts on http://localhost:8080
mvn test                   # runs all 12 tests
```

## Authentication note
Callers are identified by the `X-User-Id` header. This is a stand-in for the verified subject of a JWT /
OAuth token; in production a security filter must set the identity and the header must not be trusted.

## API
| Method | Path | Description |
|---|---|---|
| POST | `/api/transactions` | Create a transaction for the caller |
| GET | `/api/transactions` | List the caller's transactions |
| GET | `/api/transactions/{id}` | Get one (403 if not owner) |
| DELETE | `/api/transactions` | Delete the caller's own transactions only |
| POST | `/api/expenses` | Create a shared expense (EQUAL or CUSTOM split) |
| GET | `/api/users/{userId}/balances` | Pending balances with net per person (403 unless `userId` is the caller) |

Example:
```bash
curl -X POST localhost:8080/api/expenses -H 'X-User-Id: ann' -H 'Content-Type: application/json' \
  -d '{"description":"Dinner","totalAmount":120.00,"splitType":"EQUAL",
       "participants":[{"userId":"ann"},{"userId":"ben"},{"userId":"cat"}]}'
curl localhost:8080/api/users/ben/balances -H 'X-User-Id: ben'
```

## Documentation
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) - layers, data flow and diagram
- [docs/TEST_CASES.md](docs/TEST_CASES.md) - unit/integration test documentation
- [.github/copilot-instructions.md](.github/copilot-instructions.md) - Copilot project standards
