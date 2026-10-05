-- ===================================================================
-- Apex Horizon Bank - System Settings Data Script
-- ===================================================================

INSERT INTO system_settings (setting_key, setting_value, description, category, updated_at)
VALUES 
('TRANSFER_FEE_PERCENT', '0.25', 'Standard transfer service fee percentage (%)', 'CHARGES', NOW()),
('SERVICE_TAX_PERCENT', '0.10', 'Applicable banking transaction service tax (%)', 'CHARGES', NOW()),
('SAVINGS_INTEREST_RATE', '4.5', 'Annual percentage rate (APR) for Savings Accounts', 'RATES', NOW()),
('DAILY_TRANSFER_LIMIT', '50000.00', 'Default maximum daily customer transfer threshold ($)', 'LIMITS', NOW()),
('MIN_ACCOUNT_BALANCE', '50.00', 'Minimum recommended maintaining balance ($)', 'LIMITS', NOW()),
('SUPPORT_HOTLINE', '+1 (800) 555-APEX', '24/7 Priority Banking Customer Support Phone', 'CONTACT', NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();
