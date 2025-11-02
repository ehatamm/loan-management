# Loan Management MVP

A minimal, production-like full-stack MVP for managing loan repayment schedules.

## Project Structure

This is a monorepo containing:
- `/backend` - Spring Boot 3.x application (Java 21, Gradle, PostgreSQL)
- `/frontend` - React application (Vite, TypeScript, Material UI)

## Features

- Create loans with different types (Consumer, Car, Mortgage)
- Calculate repayment schedules (Annuity, Equal Principal)
- Export schedules as CSV
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

## Getting Started

See [LOCAL_DEVELOPMENT.md](LOCAL_DEVELOPMENT.md) for detailed setup and development instructions.

See [TASKS.md](TASKS.md) for the complete task breakdown and implementation phases.