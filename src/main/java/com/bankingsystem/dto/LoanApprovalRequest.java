package com.bankingsystem.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class LoanApprovalRequest {

    @NotNull(message = "Loan ID is required")
    private Long loanId;

    @NotNull(message = "Decision action is required")
    private String decision; // APPROVE or REJECT

    @DecimalMin(value = "100.00", message = "Approved amount must be positive")
    private BigDecimal approvedAmount;

    @DecimalMin(value = "1.0", message = "Interest rate must be at least 1.0%")
    @DecimalMax(value = "36.0", message = "Interest rate cannot exceed 36.0%")
    private Double interestRate;

    @Min(value = 6, message = "Tenure must be at least 6 months")
    private Integer tenureMonths;

    private String remarks;

    public LoanApprovalRequest() {
    }

    // Getters and Setters
    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
