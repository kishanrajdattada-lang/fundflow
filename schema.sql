-- Table for Users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Table for Funds (Updated to match Phase 1 Blueprint)
CREATE TABLE IF NOT EXISTS funds (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    isin VARCHAR(12) UNIQUE NOT NULL,
    currency VARCHAR(3) NOT NULL,
    nav NUMERIC(16, 4) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Seed some initial funds
INSERT INTO funds (id, name, category, isin, currency, nav)
VALUES
    ('a7727f55-f038-4d21-a3fd-72a215f847c4', 'UBS Equity Growth Fund', 'EQUITY', 'INF123456789', 'USD', 105.7600),
    ('b8838f66-0149-4e32-b4fe-83b326f958d5', 'Global Debt Income Fund', 'DEBT', 'INF987654321', 'USD', 98.4500)
ON CONFLICT (isin) DO NOTHING;
