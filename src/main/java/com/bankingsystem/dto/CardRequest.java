package com.bankingsystem.dto;

import com.bankingsystem.entity.CardType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CardRequest {

    @NotNull(message = "Card type is required")
    private CardType cardType = CardType.DEBIT;

    private String cardNetwork = "VISA";

    private BigDecimal spendingLimit = new BigDecimal("5000.00");

    public CardRequest() {
    }

    // Getters and Setters
    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getCardNetwork() {
        return cardNetwork;
    }

    public void setCardNetwork(String cardNetwork) {
        this.cardNetwork = cardNetwork;
    }

    public BigDecimal getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(BigDecimal spendingLimit) {
        this.spendingLimit = spendingLimit;
    }
}
