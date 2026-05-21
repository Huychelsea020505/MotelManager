# Motel Management System

A microservices-based boarding house management system built with Java, Micronaut, gRPC, and Docker.

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Docker Network                          │
│                                                             │
│  ┌───────────────────┐        ┌───────────────────────┐    │
│  │   room-service    │◄──────►│   billing-service     │    │
│  │  (HTTP :8080)     │  gRPC  │   (HTTP :8082)        │    │
│  │  (gRPC :9090)     │        │                       │    │
│  └────────┬──────────┘        └──────────┬────────────┘    │
│           │                              │                  │
│  ┌────────▼──────────┐        ┌──────────▼────────────┐    │
│  │     room-db       │        │     billing-db        │    │
│  │  (MySQL :3307)    │        │   (MySQL :3308)       │    │
│  └───────────────────┘        └───────────────────────┘    │
│                                                             │
└─────────────────────────────────────────────────────────────┘

         ┌──────────────────────────────┐
         │   Frontend (Vite/React)      │
         │        (HTTP :5173)          │
         │  runs outside Docker         │
         └──────────────────────────────┘
```

## Services

| Service | Port (HTTP) | Port (gRPC) | Database |
|---|---|---|---|
| `room-service` | 8080 | 9090 | room-db (MySQL) |
| `billing-service` | 8082 | — | billing-db (MySQL) |
| Frontend | 5173 | — | — |

---

## Prerequisites

Install the following before running the project:

| Tool | Version | Download |
|---|---|---|
| Docker Desktop | 24+ | https://www.docker.com/products/docker-desktop |
| Docker Compose | v2 (bundled with Docker Desktop) | — |
| Java JDK | 17 | https://adoptium.net |
| Node.js | 18+ | https://nodejs.org |
| pnpm | latest | `npm install -g pnpm` |

> **Note:** Java and Node.js are only required if you want to run services outside Docker.

---

## Libraries & Technologies

### Backend (both services)

| Library | Version | Purpose |
|---|---|---|
| Micronaut | 4.x | HTTP server framework |
| Micronaut Data JPA | — | ORM / repository layer |
| Hibernate | — | JPA implementation |
| gRPC Java | 1.68.1 | Inter-service communication |
| Protocol Buffers | 3.25.5 | gRPC message serialization |
| MySQL Connector/J | — | MySQL JDBC driver |
| HikariCP | — | Database connection pooling |
| Logback | — | Logging |
| Gradle (Kotlin DSL) | — | Build tool |
| Shadow JAR plugin | — | Fat JAR packaging |

### Frontend

| Library | Purpose |
|---|---|
| Vite | Build tool / dev server |
| React | UI framework |
| pnpm | Package manager |

### Infrastructure

| Tool | Purpose |
|---|---|
| Docker | Containerization |
| Docker Compose | Multi-container orchestration |
| MySQL 8.4 | Relational database (one instance per service) |

---

## gRPC Design

The `room-service` exposes a gRPC server on port **9090**.  
The `billing-service` acts as a gRPC client and calls `room-service` over the Docker network.

### Proto File: `room-service/src/main/proto/room.proto`

```protobuf
service RoomGrpcService {
  // Unary RPC — fetch a single room by ID
  rpc GetRoomById(RoomRequest) returns (RoomResponse);

  // Server-Streaming RPC — stream all available rooms
  rpc StreamAvailableRooms(Empty) returns (stream RoomResponse);
}
```

| RPC | Type | Description |
|---|---|---|
| `GetRoomById` | **Unary** | billing-service calls this to validate a room when creating an invoice |
| `StreamAvailableRooms` | **Server Streaming** | billing-service calls this to count available rooms for dashboard |

---

## Running the System

### Option 1: Docker (Recommended — runs everything together)

**Step 1** — Open a terminal at the project root (where `docker-compose.yml` is located):

```bash
docker compose up -d
```

This command will:
- Build Docker images for `room-service` and `billing-service`
- Start `room-db` and `billing-db` (MySQL) containers
- Start both services after their databases are healthy
- Connect everything through Docker's internal network

**Step 2** — Verify all containers are running:

```bash
docker compose ps
```

Expected output:
```
NAME               STATUS          PORTS
room-db            Up (healthy)    0.0.0.0:3307->3306/tcp
billing-db         Up (healthy)    0.0.0.0:3308->3306/tcp
room-service       Up              0.0.0.0:8080->8080/tcp, 0.0.0.0:9090->9090/tcp
billing-service    Up              0.0.0.0:8082->8082/tcp
```

**Step 3** — Start the Frontend (runs outside Docker):

```bash
cd frontend
pnpm install
pnpm dev
```

Open the app at: http://localhost:5173

**To stop all containers:**

```bash
docker compose down
```

---

### Option 2: Run Services Locally (Development)

> Requires Java 17 and a running MySQL instance.

**Step 1** — Start only the databases via Docker:

```bash
docker compose up -d room-db billing-db
```

**Step 2** — Run `room-service`:

```bash
# Windows
cd room-service
gradlew.bat run

# Mac / Linux
cd room-service
./gradlew run
```

**Step 3** — Run `billing-service` in a new terminal:

```bash
# Windows
cd billing-service
gradlew.bat run

# Mac / Linux
cd billing-service
./gradlew run
```

**Step 4** — Run the Frontend in another terminal:

```bash
cd frontend
pnpm install
pnpm dev
```

---

## API Endpoints

### room-service — http://localhost:8080

| Method | Endpoint | Description |
|---|---|---|
| GET | `/rooms` | List all rooms |
| POST | `/rooms` | Create a room |
| GET | `/rooms/{id}` | Get room by ID |
| PUT | `/rooms/{id}` | Update room |
| DELETE | `/rooms/{id}` | Delete room |
| GET | `/rooms/occupied` | List occupied rooms |
| GET | `/tenants` | List all tenants |
| POST | `/tenants` | Create a tenant |
| GET | `/tenants/{id}` | Get tenant by ID |
| PUT | `/tenants/{id}` | Update tenant |
| DELETE | `/tenants/{id}` | Delete tenant |

### billing-service — http://localhost:8082

| Method | Endpoint | Description |
|---|---|---|
| GET | `/invoices` | List all invoices |
| POST | `/invoices` | Create an invoice (validates room via gRPC) |
| GET | `/invoices/unpaid` | List unpaid invoices |
| PUT | `/invoices/{id}/pay` | Mark invoice as paid |
| GET | `/dashboard` | Dashboard stats (uses gRPC streaming for room count) |

---

## Seed Data

Both databases are auto-initialized by Micronaut Data JPA on startup.

**Rooms (room-db):**
- A101 — 2,500,000 VND — 20m² — Occupied
- A102 — 2,300,000 VND — 18m² — Available
- B201 — 3,000,000 VND — 25m² — Occupied

**Tenants (room-db):**
- Nguyen Van An (Room A101)
- Tran Thi Binh (Room B201)

**Invoices (billing-db):**
- May 2026 — Unpaid
- April 2026 — Paid

---

## Troubleshooting

**Containers fail to start:**
```bash
# Check logs of a specific service
docker compose logs room-service
docker compose logs billing-service
```

**Port already in use:**
```bash
# Stop and remove all containers, then restart
docker compose down
docker compose up -d
```

**Rebuild images after code changes:**
```bash
docker compose up -d --build
```

**Remove orphan containers from previous runs:**
```bash
docker compose up -d --remove-orphans
```
