package com.bankingsystem.dto;

import com.bankingsystem.entity.LoanType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class LoanApplicationRequest {

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "500.00", message = "Minimum loan amount is $500.00")
    @DecimalMax(value = "1000000.00", message = "Maximum loan amount is $1,000,000.00")
    private BigDecimal requestedAmount;

    @NotNull(message = "Tenure in months is required")
    @Min(value = 6, message = "Minimum tenure is 6 months")
    @Max(value = 360, message = "Maximum tenure is 360 months (30 years)")
    private Integer tenureMonths;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "100.00", message = "Please enter your valid monthly income")
    private BigDecimal monthlyIncome;

    @NotBlank(message = "Employment type is required")
    private String employmentType;

    @NotBlank(message = "Loan purpose is required")
    @Size(min = 5, max = 255, message = "Purpose must be between 5 and 255 characters")
    private String purpose;

    public LoanApplicationRequest() {
    }

    // Getters and Setters
    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
