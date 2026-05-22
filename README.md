# FundFlow

A very small FundFlow starter app using Java 17, Spring Boot 3.5.0, and Postgres.

## Setup

1. Create a Postgres database named `fundflow`.
2. Export database settings or update `src/main/resources/application.properties`.
3. Start the app:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/fundflow
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres
mvn spring-boot:run
```

4. Open `http://localhost:8080`.

## Endpoints

- `GET /` opens the basic FundFlow page.
- `GET /swagger-ui.html` opens Swagger UI.
- `GET /v3/api-docs` returns the OpenAPI JSON.
- `GET /health` checks the Postgres connection.
- `GET /api/funds` returns funds as JSON.
- `POST /api/funds` creates a fund from JSON.

Example:

```bash
curl -X POST http://localhost:8080/api/funds \
  -H "Content-Type: application/json" \
  -d '{"name":"Community Seed Fund","amount":25000,"status":"active"}'
```
