package com.bankingsystem.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class DepositRequest {

    @NotNull(message = "Deposit amount is required")
    @DecimalMin(value = "5.00", message = "Minimum deposit amount is $5.00")
    @DecimalMax(value = "100000.00", message = "Maximum single deposit amount is $100,000.00")
    private BigDecimal amount;

    private String paymentMethod = "ONLINE_TRANSFER"; // ONLINE_TRANSFER, WIRE, UPI, CASH_DEPOSIT

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description = "Standard Account Deposit";

    public DepositRequest() {
    }

    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
