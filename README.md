# Personal Finance API

A clean Spring Boot backend for personal finance tracking with JWT authentication, account management, transaction tracking, and spending analytics.

Built with Java 17, Spring Boot 3, PostgreSQL, Spring Security, JPA, and Swagger/OpenAPI.

## Overview

This project provides a REST API for:

- user registration and login
- account creation and account lookup
- income and expense transaction tracking
- monthly and category-based spending analytics
- centralized JSON error handling

The codebase follows a simple clean-layered structure with separate packages for controllers, services, repositories, entities, DTOs, config, exceptions, and utilities.

## Tech Stack

- Java 17
- Maven
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (`jjwt`)
- PostgreSQL
- Lombok
- Jakarta Validation
- Springdoc OpenAPI / Swagger UI
- H2 for tests

## Project Structure

```text
src/main/java/com/portfolio/finance
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── service
└── util
```

## Features

### Authentication

- `POST /auth/register`
- `POST /auth/login`
- JWT token generation
- BCrypt password hashing
- custom `UserDetailsService`

### Accounts

- `POST /accounts`
- `GET /accounts`
- `GET /accounts/{id}`

### Transactions

- `POST /transactions`
- `GET /transactions`
- `GET /transactions/{id}`

### Analytics

- `GET /analytics/monthly`
- `GET /analytics/categories`

### System

- `GET /api/v1/health`
- Swagger UI at `/swagger-ui.html`

## Security

All endpoints are protected except:

- `/auth/**`

Authentication uses Bearer tokens:

```http
Authorization: Bearer <jwt-token>
```

## Configuration

Application configuration is defined in [application.yml](/Users/huseynbva/personal-finance-api/src/main/resources/application.yml).

Environment variables:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=personal_finance
DB_USERNAME=postgres
DB_PASSWORD=postgres
SERVER_PORT=8080
JWT_SECRET_BASE64=your-base64-encoded-secret
JWT_EXPIRATION_MS=3600000
```

Notes:

- `JWT_SECRET_BASE64` should be a strong Base64-encoded secret in real environments.
- `spring.jpa.hibernate.ddl-auto=update` is currently enabled for convenience.

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/HuseynBlv/personal-finance-api.git
cd personal-finance-api
```

### 2. Start PostgreSQL

Create a database named:

```text
personal_finance
```

### 3. Export environment variables

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=personal_finance
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export JWT_SECRET_BASE64=$(printf '%s' 'replace-this-with-a-long-random-secret-key-1234567890' | base64)
export JWT_EXPIRATION_MS=3600000
```

### 4. Run the application

```bash
mvn spring-boot:run
```

The API will start on:

```text
http://localhost:8080
```

## Running Tests

```bash
mvn test
```

Tests use H2 in-memory configuration from [application-test.yml](/Users/huseynbva/personal-finance-api/src/test/resources/application-test.yml).

## API Quick Start

### Register

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123"
  }'
```

### Create Account

```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Main Wallet",
    "balance": 1500.00,
    "currency": "USD",
    "userId": 1
  }'
```

### Create Transaction

```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "accountId": 1,
    "amount": 50.00,
    "category": "Food",
    "type": "EXPENSE",
    "description": "Lunch",
    "date": "2026-03-27"
  }'
```

### Get Monthly Spending

```bash
curl "http://localhost:8080/analytics/monthly?userId=1&month=2026-03" \
  -H "Authorization: Bearer <token>"
```

### Get Category Spending

```bash
curl "http://localhost:8080/analytics/categories?userId=1&startDate=2026-03-01&endDate=2026-03-31" \
  -H "Authorization: Bearer <token>"
```

## API Reference

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Error Response Format

The API uses centralized exception handling and returns clean JSON responses:

```json
{
  "timestamp": "2026-03-27T12:34:56.789Z",
  "status": 404,
  "error": "Resource not found",
  "message": "Account not found"
}
```

## Domain Model

- `User` -> owns many `Account`
- `Account` -> belongs to one `User`
- `Account` -> owns many `Transaction`
- `Transaction` -> belongs to one `Account`

Transaction types:

- `INCOME`
- `EXPENSE`

## Current Notes

- JWT auth is implemented and active.
- Services contain the main business logic.
- DTOs are used for API input/output instead of exposing entities directly.
- Some endpoints currently rely on explicit `userId` and `accountId` request parameters rather than deriving ownership from the authenticated principal.

## License

This project currently has no explicit license file.
