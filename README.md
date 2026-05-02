# Motel Management System

Backend REST API demo for managing motel rooms, tenants, invoices, payments, and dashboard reports.

## Technologies

- Java 17
- Micronaut
- Micronaut Data JPA
- Hibernate
- H2 in-memory database
- Gradle Kotlin DSL

## Run

```bash
./gradlew run
```

On Windows:

```bash
gradlew.bat run
```

The app uses an H2 in-memory database and creates demo data automatically at startup.

Swagger UI is available after startup:

```text
http://localhost:8080/swagger/views/swagger-ui/index.html
```

## Run With Docker MySQL

Start MySQL and Adminer:

```bash
docker compose up -d
```

Run the Micronaut app with the Docker database profile:

```bash
gradlew.bat run -Dmicronaut.environments=docker
```

Database design and sample rows are documented in `docs/database-design.md`.

## Demo Account

```text
username: admin
password: 123456
```

## API

```text
POST   /auth/login

GET    /rooms
POST   /rooms
GET    /rooms/{id}
PUT    /rooms/{id}
DELETE /rooms/{id}
GET    /rooms/occupied

GET    /tenants
POST   /tenants
GET    /tenants/{id}
PUT    /tenants/{id}
DELETE /tenants/{id}

GET    /invoices
POST   /invoices
GET    /invoices/unpaid
PUT    /invoices/{id}/pay
GET    /payments/history

GET    /dashboard
```

## Demo Flow

1. Login with `admin / 123456`.
2. View room list.
3. Create a new room.
4. Add a tenant to an available room.
5. Create a monthly invoice.
6. View unpaid invoices.
7. Pay an invoice.
8. View dashboard revenue.

## Seed Data

Rooms:

- A101, 2,500,000 VND, 20m2, occupied
- A102, 2,300,000 VND, 18m2, available
- B201, 3,000,000 VND, 25m2, occupied

Tenants:

- Nguyen Van An
- Tran Thi Binh

Invoices:

- 05/2026 unpaid invoice
- 04/2026 paid invoice

How to run
Bước 1: Khởi động Database (MySQL)

Mở terminal ở thư mục gốc của project (nơi chứa docker-compose.yml).
Chạy: docker-compose up -d
Bước 2: Chạy Backend (Java/Micronaut)

Vẫn ở thư mục gốc, chạy lệnh:
Trên Windows: gradlew.bat run
Trên Mac/Linux: ./gradlew run
Bước 3: Chạy Frontend (Vite)

Mở một terminal mới, di chuyển vào thư mục frontend: cd frontend
Cài đặt các thư viện: npm install
Chạy giao diện: npm run dev