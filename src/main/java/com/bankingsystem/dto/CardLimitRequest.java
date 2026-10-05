package com.bankingsystem.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CardLimitRequest {

    @NotNull(message = "Card ID is required")
    private Long cardId;

    @NotNull(message = "Spending limit is required")
    @DecimalMin(value = "100.00", message = "Minimum spending limit is $100.00")
    @DecimalMax(value = "50000.00", message = "Maximum spending limit is $50,000.00")
    private BigDecimal spendingLimit;

    @NotNull(message = "Daily limit is required")
    @DecimalMin(value = "50.00", message = "Minimum daily limit is $50.00")
    @DecimalMax(value = "20000.00", message = "Maximum daily limit is $20,000.00")
    private BigDecimal dailyLimit;

    public CardLimitRequest() {
    }

    // Getters and Setters
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public BigDecimal getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(BigDecimal spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }
}
