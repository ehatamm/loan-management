-- Create loans table
CREATE TABLE loans (
    id UUID PRIMARY KEY,
    loan_type VARCHAR(20) NOT NULL CHECK (loan_type IN ('CONSUMER', 'CAR', 'MORTGAGE')),
    amount NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    period_months INTEGER NOT NULL CHECK (period_months > 0),
    annual_interest_rate NUMERIC(5, 2) NOT NULL CHECK (annual_interest_rate >= 0 AND annual_interest_rate <= 100),
    schedule_type VARCHAR(20) NOT NULL CHECK (schedule_type IN ('ANNUITY', 'EQUAL_PRINCIPAL')),
    start_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Create index on loan_type for filtering queries
CREATE INDEX idx_loans_loan_type ON loans(loan_type);

-- Create index on start_date for date range queries
CREATE INDEX idx_loans_start_date ON loans(start_date);

