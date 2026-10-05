package com.bankingsystem.entity;

public enum LoanType {
    PERSONAL_LOAN("Personal Loan", 10.5),
    HOME_LOAN("Home Loan", 8.2),
    EDUCATION_LOAN("Education Loan", 7.5),
    VEHICLE_LOAN("Vehicle Loan", 9.0);

    private final String displayName;
    private final double defaultInterestRate;

    LoanType(String displayName, double defaultInterestRate) {
        this.displayName = displayName;
        this.defaultInterestRate = defaultInterestRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultInterestRate() {
        return defaultInterestRate;
    }
}
