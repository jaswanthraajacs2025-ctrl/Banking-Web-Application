package com.bankingsystem.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class WithdrawRequest {

    @NotNull(message = "Withdrawal amount is required")
    @DecimalMin(value = "5.00", message = "Minimum withdrawal amount is $5.00")
    @DecimalMax(value = "25000.00", message = "Maximum single withdrawal amount is $25,000.00")
    private BigDecimal amount;

    private String method = "ATM_ELECTRONIC";

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description = "Standard Cash Withdrawal";

    public WithdrawRequest() {
    }

    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
