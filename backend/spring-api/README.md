# Spring Boot API

Production Systems REST API for devices, test runs, and live execution control.

## Run locally

- Ensure PostgreSQL is running.
- Set env vars:
  - DB_URL=jdbc:postgresql://localhost:5432/production_systems
  - DB_USER=postgres
  - DB_PASSWORD=postgres
- Start:
  - mvn spring-boot:run

## Core endpoints

### Devices

- POST /api/devices
- GET /api/devices
- GET /api/devices/{id}/test-runs
- GET /api/devices/{id}/live-output (SSE)

### Test runs

- POST /api/test-runs
- GET /api/test-runs
- GET /api/test-runs/{id}
- GET /api/test-runs/latest

### Test executions (remote trigger)

- POST /api/devices/{deviceId}/run-test
- GET /api/test-executions/next
- GET /api/test-executions/{id}
- PATCH /api/test-executions/{id}

### Metrics

- GET /api/metrics

## Swagger UI

- Swagger UI: /api/swagger
- OpenAPI JSON: /api/docs
