-- ===================================================================
-- Apex Horizon Bank - MySQL Database Schema
-- ===================================================================

CREATE DATABASE IF NOT EXISTS online_banking;
USE online_banking;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(20),
    date_of_birth DATE,
    address VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 2. Bank Accounts Table
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(30) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    account_type VARCHAR(20) NOT NULL DEFAULT 'SAVINGS',
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    available_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    daily_transfer_limit DECIMAL(15, 2) DEFAULT 50000.00,
    branch_name VARCHAR(100) DEFAULT 'Apex Horizon Main Branch',
    ifsc_code VARCHAR(30) DEFAULT 'APEX0001099',
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_number VARCHAR(40) NOT NULL UNIQUE,
    account_id BIGINT NOT NULL,
    sender_account VARCHAR(30),
    receiver_account VARCHAR(30),
    type VARCHAR(30) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    fee DECIMAL(15, 2) DEFAULT 0.00,
    tax DECIMAL(15, 2) DEFAULT 0.00,
    total_amount DECIMAL(15, 2) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    balance_after DECIMAL(15, 2),
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_txn_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- 4. Beneficiaries Table
CREATE TABLE IF NOT EXISTS beneficiaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    account_number VARCHAR(30) NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    ifsc VARCHAR(30),
    nickname VARCHAR(50),
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_beneficiary_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. Payment Cards Table
CREATE TABLE IF NOT EXISTS cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    card_type VARCHAR(20) NOT NULL DEFAULT 'DEBIT',
    card_number_masked VARCHAR(30) NOT NULL,
    card_holder_name VARCHAR(100) NOT NULL,
    expiry_date VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    spending_limit DECIMAL(15, 2) DEFAULT 5000.00,
    daily_limit DECIMAL(15, 2) DEFAULT 2000.00,
    card_network VARCHAR(30) DEFAULT 'VISA',
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_card_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_card_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- 6. Loans Table
CREATE TABLE IF NOT EXISTS loans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    loan_type VARCHAR(30) NOT NULL,
    requested_amount DECIMAL(15, 2) NOT NULL,
    approved_amount DECIMAL(15, 2),
    interest_rate DOUBLE,
    tenure_months INT NOT NULL,
    monthly_income DECIMAL(15, 2),
    employment_type VARCHAR(50),
    purpose VARCHAR(255),
    remarks VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    monthly_emi DECIMAL(15, 2),
    total_payable DECIMAL(15, 2),
    applied_at DATETIME NOT NULL,
    reviewed_at DATETIME,
    CONSTRAINT fk_loan_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 7. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(30) DEFAULT 'INFO',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    link VARCHAR(150),
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 8. Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(120),
    action VARCHAR(40) NOT NULL,
    description VARCHAR(500) NOT NULL,
    ip_address VARCHAR(50),
    status VARCHAR(20) DEFAULT 'SUCCESS',
    timestamp DATETIME NOT NULL
);

-- 9. System Settings Table
CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(50) NOT NULL UNIQUE,
    setting_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    category VARCHAR(50) DEFAULT 'GENERAL',
    updated_at DATETIME
);
