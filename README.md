# Loan Management MVP

A minimal, production-like full-stack MVP for managing loan repayment schedules.

**📋 For architectural decisions and design rationale, see [DECISIONS.md](DECISIONS.md).**

## Project Structure

This is a monorepo containing:
- `/backend` - Spring Boot 3.x application (Java 21, Gradle, PostgreSQL)
- `/frontend` - React application (Vite, TypeScript, Material UI)

## Features

- Create loans with different types (Consumer, Car, Mortgage)
- Calculate repayment schedules (Annuity, Equal Principal)
- RESTful API with proper validation and error handling

## Tech Stack

**Backend:**
- Spring Boot 3.x
- Java 21
- Gradle (Kotlin DSL)
- PostgreSQL 16+
- Flyway for database migrations

**Frontend:**
- React 18+
- Vite
- TypeScript
- Material UI

## Quick Start with Docker

The easiest way to run the application is using Docker Compose:

```bash
docker-compose up --build
```

This will start:
- PostgreSQL database on port 5432
- Backend API on http://localhost:8080/api
- Frontend application on http://localhost

For more details, see [DOCKER.md](DOCKER.md).