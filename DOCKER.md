# Docker Setup

This project includes a complete Docker Compose setup for running the full stack locally without any manual configuration.

## Prerequisites

- Docker and Docker Compose installed
- Ports 80, 8080, and 5432 available on your machine

**Note**: Port 80 may require elevated privileges (sudo) on some systems. If you encounter permission issues, you can modify the port mapping in `docker-compose.yml`.

## Quick Start

Build and start all services:

```bash
docker-compose up --build
```

Start in detached mode (runs in background):

```bash
docker-compose up -d --build
```

View logs:

```bash
docker-compose logs -f
```

Stop all services:

```bash
docker-compose down
```

Stop and remove volumes (deletes database data):

```bash
docker-compose down -v
```

Rebuild after code changes:

```bash
docker-compose up --build
```

## Services

Once started, access the application at:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **PostgreSQL**: localhost:5432

## Service Details

### Database (db)
- **Image**: postgres:16
- **Port**: 5432
- **Database**: loans
- **Credentials**: postgres/postgres
- **Volume**: Persistent data stored in `postgres_data` volume

### Backend
- **Build**: Multi-stage Docker build
- **Runtime**: Eclipse Temurin 21 JRE (Alpine)
- **Port**: 8080
- **Health Check**: Verifies API endpoint is accessible
- **Depends on**: Database (waits for database to be healthy)

### Frontend
- **Build**: Multi-stage Docker build (Node.js → Nginx)
- **Runtime**: Nginx Alpine
- **Port**: 80
- **Features**: 
  - Serves React application
  - Proxies `/api` requests to backend
  - SPA routing support

## Architecture

```
┌─────────┐     ┌──────────┐     ┌──────┐
│Browser │────▶│Frontend │────▶│Backend│────▶│  DB  │
│        │     │ (Nginx)  │     │(Spring)│     │(PG)  │
└────────┘     └──────────┘     └──────┘     └──────┘
   :80           :80              :8080      :5432
```

The frontend is served via Nginx and proxies all `/api/*` requests to the backend service.

## Health Checks

All services include health checks to ensure proper startup order:
- **Database**: Checks PostgreSQL readiness with `pg_isready`
- **Backend**: Verifies API endpoint responds (waits up to 60s for startup)
- **Frontend**: Checks Nginx is serving content

Services wait for dependencies to be healthy before starting.

## Troubleshooting

**Frontend not accessible:**
- Check if port 80 is available: `netstat -an | grep :80` or `lsof -i :80`
- If port is in use, modify the port mapping in `docker-compose.yml`
- Ensure containers are running: `docker-compose ps`

**Backend not starting:**
- Check database is healthy: `docker-compose logs db`
- Check backend logs: `docker-compose logs backend`
- Verify database connection string in docker-compose.yml environment variables

**Rebuild after code changes:**
- Use `docker-compose up --build` to force rebuild of changed services
- Or rebuild specific service: `docker-compose build backend` then `docker-compose up -d`

