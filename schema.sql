CREATE TABLE IF NOT EXISTS funds (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    status TEXT NOT NULL DEFAULT 'planned',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO funds (name, amount, status)
VALUES
    ('Community Seed Fund', 25000.00, 'active'),
    ('Emergency Reserve', 10000.00, 'planned')
ON CONFLICT DO NOTHING;
