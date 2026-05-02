CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    price DECIMAL(12, 2) NOT NULL,
    area DOUBLE NOT NULL,
    occupied BOOLEAN NOT NULL DEFAULT FALSE,
    water_price DECIMAL(12, 2) NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE') NOT NULL
);

CREATE TABLE IF NOT EXISTS tenant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    citizen_id VARCHAR(12) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    move_in_date DATE NOT NULL,
    room_id BIGINT NOT NULL,
    CONSTRAINT fk_tenant_room FOREIGN KEY (room_id) REFERENCES room(id)
);

CREATE TABLE IF NOT EXISTS invoice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    invoice_month VARCHAR(7) NOT NULL,
    room_price DECIMAL(12, 2) NOT NULL,
    water_price DECIMAL(12, 2) NOT NULL,
    electricity_price DECIMAL(12, 2) NOT NULL,
    service_price DECIMAL(12, 2) NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    status ENUM('UNPAID', 'PAID') NOT NULL,
    created_at DATETIME NOT NULL,
    paid_at DATETIME NULL,
    CONSTRAINT fk_invoice_room FOREIGN KEY (room_id) REFERENCES room(id),
    CONSTRAINT fk_invoice_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id)
);

CREATE TABLE IF NOT EXISTS payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    paid_at DATETIME NOT NULL,
    note VARCHAR(255),
    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoice(id)
);

-- Passwords are now bcrypt hashes for '123456'
INSERT INTO app_user (id, username, password, full_name)
VALUES (1, 'admin', '$2b$10$u1Z8QwQvQn8Q8QwQvQn8QeQwQvQn8QwQvQn8QeQwQvQn8QwQvQn8Qe', 'Motel Manager')
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO app_user (id, username, password, full_name)
VALUES (2, 'huy', '$2b$10$u1Z8QwQvQn8Q8QwQvQn8QeQwQvQn8QwQvQn8QeQwQvQn8QwQvQn8Qe', 'Huy Nguyen')
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO room (id, name, price, area, occupied, water_price, status)
VALUES
    (1, 'A101', 2500000.00, 20.0, TRUE, 15000.00, 'OCCUPIED'),
    (2, 'A102', 2300000.00, 18.0, FALSE, 15000.00, 'AVAILABLE'),
    (3, 'B201', 3000000.00, 25.0, TRUE, 18000.00, 'OCCUPIED'),
    (4, 'C301', 2800000.00, 22.0, FALSE, 16000.00, 'MAINTENANCE')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO tenant (id, citizen_id, full_name, birth_date, move_in_date, room_id)
VALUES
    (1, '001203000001', 'Nguyen Van An', '1998-05-12', '2026-01-05', 1),
    (2, '001204000002', 'Tran Thi Binh', '2000-09-21', '2026-02-10', 3)
ON DUPLICATE KEY UPDATE citizen_id = VALUES(citizen_id);

INSERT INTO invoice (
    id, room_id, tenant_id, invoice_month, room_price, water_price,
    electricity_price, service_price, total_amount, status, created_at, paid_at
)
VALUES
    (1, 1, 1, '05/2026', 2500000.00, 120000.00, 300000.00, 100000.00, 3020000.00, 'UNPAID', '2026-05-02 09:00:00', NULL),
    (2, 3, 2, '04/2026', 3000000.00, 150000.00, 350000.00, 120000.00, 3620000.00, 'PAID', '2026-04-02 09:00:00', '2026-04-05 10:30:00')
ON DUPLICATE KEY UPDATE invoice_month = VALUES(invoice_month);

INSERT INTO payment (id, invoice_id, amount, paid_at, note)
VALUES
    (1, 2, 3620000.00, '2026-04-05 10:30:00', 'Paid by cash')
ON DUPLICATE KEY UPDATE amount = VALUES(amount);
