# JobPortal

A job portal backend built as a **modular monolith** — feature-oriented modules with
enforced boundaries, packaged as a single deployable, backed by real supporting
infrastructure (Postgres, and later Redis, Elasticsearch, RabbitMQ).

## Why a modular monolith

Microservices trade operational simplicity for independent scalability and deployment.
At this project's scale, that trade isn't worth it yet. Module boundaries are enforced
at build time with Spring Modulith, so any module *could* be extracted into its own
service later without a rewrite — the seams already exist from day one.

## Planned architecture


com.jobportal

├── auth/ authentication & authorization

├── users/ user profiles

├── jobs/ job postings

├── applications/ candidate applications

├── search/ Elasticsearch-backed job search

├── notifications/ async email / in-app notifications

├── resumes/ resume upload & parsing

├── analytics/ reporting & metrics

└── common/ shared config, exceptions, utilities


Each module exposes a `*Service` interface as its only public API. Cross-module calls
go through that interface — never through another module's repository or entity classes.
Verified automatically via `ApplicationModules.verify()`.

## Tech stack

| Concern              | Choice                          |
|-----------------------|----------------------------------|
| Framework             | Spring Boot 4 / Java 21          |
| Build tool             | Gradle (Kotlin DSL)              |
| Module boundaries     | Spring Modulith                  |
| Persistence            | PostgreSQL + Spring Data JPA     |
| Schema migrations      | Flyway                           |
| Testing                 | JUnit 5, Testcontainers          |
| Observability            | Spring Boot Actuator + Micrometer|

## Running locally

Requires Docker Desktop running.

```powershell
docker compose up -d
.\gradlew.bat bootRun
```

## Testing

```powershell
.\gradlew.bat build
```

Runs unit tests and spins up a real Postgres instance via Testcontainers for
integration-level checks — no mocked database.

## Status

🚧 Early scaffolding stage — modules are being built out incrementally, one at a time,
each in its own PR with the corresponding Flyway migrations and tests.