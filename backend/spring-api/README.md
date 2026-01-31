# Spring Boot API

## Run locally

- Ensure PostgreSQL is running.
- Set env vars:
  - DB_URL=jdbc:postgresql://localhost:5432/production_systems
  - DB_USER=postgres
  - DB_PASSWORD=postgres
- Start:
  - mvn spring-boot:run

## Endpoints

- POST /api/devices
- POST /api/test-runs
- GET /api/test-runs
- GET /api/metrics

## Swagger UI

- Swagger UI: /api/swagger
- OpenAPI JSON: /api/docs
