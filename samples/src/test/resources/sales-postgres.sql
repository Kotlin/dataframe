-- The table of sales.csv in PostgreSQL: simple column types only (text, integer, double precision).
-- Both halves of the PostgreSQL cases (PandasPostgresCasesTest.kt and validate_pandas.py) run this file in a schema
-- of their own, so that the queries in pandas-postgres-cases.json can say plain `sales`.
CREATE TABLE sales (
    region   text             NOT NULL,
    product  text             NOT NULL,
    year     integer          NOT NULL,
    revenue  integer          NOT NULL,
    cost     integer          NOT NULL,
    discount double precision
);

INSERT INTO sales (region, product, year, revenue, cost, discount) VALUES
    ('North', 'Pen', 2023, 100,  60, 0.1),
    ('North', 'Ink', 2023, 150,  90, NULL),
    ('South', 'Pen', 2023,  80,  70, 0.05),
    ('South', 'Ink', 2024, 200, 120, NULL),
    ('North', 'Pen', 2024, 120,  70, 0.2),
    ('South', 'Pen', 2024,  90,  50, 0.0);
