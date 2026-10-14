# Copilot Custom Instructions - FinTrack API

These rules apply to every Copilot feature (inline suggestions, Ask, Edit, Agent, slash commands).
FinTrack handles money: correctness, auditability and security outrank brevity.

## 1. Technology stack
- Java 17, Spring Boot 3.3.x, Maven.
- ORM: Spring Data JPA / Hibernate. Never use raw JDBC or string-built SQL.
- Database: H2 for dev/test, PostgreSQL in production (configured through environment variables).
- Tests: JUnit 5, Spring Boot Test, MockMvc.

## 2. Architecture conventions
- Strict layers, one direction only: Controller -> Service -> Repository -> Entity.
- Controller: HTTP only (parse, validate, call service, map to response DTO). No business logic.
- Service: business rules and authorisation. No EntityManager, no JPQL, no HTTP types.
- Repository: Spring Data interfaces; JPQL only with named parameters.
- Pure business logic (split and balance calculation) lives in framework-free classes.
- Constructor injection only; no field injection.
- Request/response DTOs are records; never return entities from controllers.

## 3. Coding standards
- Money is `BigDecimal` with scale 2. Never `float` or `double`. Compare with `compareTo`.
- Classes PascalCase, methods and fields camelCase, constants UPPER_SNAKE_CASE.
- Public classes and methods have Javadoc (purpose, parameters, exceptions).
- Use SLF4J with `event=name key=value` structured messages. No `System.out`.
- Throw specific exceptions (NotFound, Forbidden, BusinessValidation); never catch bare `Exception` except in the central handler.

## 4. Security rules
- Users may only read or modify their own data; check ownership in the service layer.
- Validate all inputs with Jakarta Bean Validation plus business rules.
- No hardcoded secrets or credentials; read them from environment variables.
- Never log PII, full card or account numbers, or secrets.
- Error responses must not leak stack traces or internals.

## 5. Testing expectations
- Every public service method has unit tests: happy path, edge cases, error cases.
- Cover authorisation (access to another user's data must fail).
- Rounding and money tests are mandatory for anything that splits amounts.
- Tests are deterministic and independent; no sleeps and no real external services.
