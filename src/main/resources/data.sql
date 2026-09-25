-- Initial seed data for Employee Service
INSERT INTO employees (first_name, last_name, email, department, salary, hire_date, status, created_at, updated_at)
VALUES 
('Alice', 'Johnson', 'alice.johnson@nashtechglobal.com', 'ENGINEERING', 95000.00, '2023-01-10', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bob', 'Smith', 'bob.smith@nashtechglobal.com', 'ENGINEERING', 105000.00, '2022-03-15', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Carol', 'Williams', 'carol.williams@nashtechglobal.com', 'HR', 72000.00, '2023-06-01', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('David', 'Brown', 'david.brown@nashtechglobal.com', 'FINANCE', 88000.00, '2021-11-20', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Emma', 'Davis', 'emma.davis@nashtechglobal.com', 'MARKETING', 68000.00, '2024-02-01', 'PROBATION', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Frank', 'Miller', 'frank.miller@nashtechglobal.com', 'SALES', 75000.00, '2022-08-14', 'ON_LEAVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Grace', 'Wilson', 'grace.wilson@nashtechglobal.com', 'ENGINEERING', 115000.00, '2020-05-18', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
