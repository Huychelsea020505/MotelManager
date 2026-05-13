# Boarding House Manager

Backend services and React frontend for managing rooms, tenants, invoices, payments, and dashboard data.

## Services

- `room-service`: Micronaut service on `http://localhost:8080`, gRPC on `9090`
- `billing-service`: Micronaut service on `http://localhost:8082`
- `frontend`: Vite React app on `http://localhost:5173`
- `room-db`: MySQL on host port `3307`
- `billing-db`: MySQL on host port `3308`

## Recommended Run

Start the backend stack from the project root:

```powershell
docker compose up -d
```

Check that the backend is running:

```powershell
docker compose ps
```

Start the frontend in another terminal:

```powershell
cd frontend
pnpm dev
```

Open:

```text
http://localhost:5173
```

## Important

Do not run `room-service` with Gradle while the Docker `room-service` container is running. Both use port `9090` for gRPC, so Gradle will fail with:

```text
Address already in use: bind
```

If you want to debug `room-service` locally with Gradle, stop the Docker container first:

```powershell
docker compose stop room-service billing-service
cd room-service
.\gradlew.bat run
```

Or run it on other HTTP and gRPC ports:

```powershell
cd room-service
$env:MICRONAUT_SERVER_PORT=8081
$env:GRPC_SERVER_PORT=9091
.\gradlew.bat run
```

When switching between Docker and Gradle, restart `pnpm dev` so Vite reloads its proxy config.

## API

Room service:

```text
GET    /rooms
POST   /rooms
GET    /rooms/{id}
PUT    /rooms/{id}
DELETE /rooms/{id}
GET    /tenants
POST   /tenants
```

Billing service:

```text
POST   /auth/login
GET    /dashboard
GET    /invoices
POST   /invoices
PUT    /invoices/{id}/pay
GET    /payments/history
```

## Demo Account

```text
username: admin
password: 123456
```
