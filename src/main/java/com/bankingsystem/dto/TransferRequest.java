package com.bankingsystem.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TransferRequest {

    @NotBlank(message = "Recipient account number is required")
    private String recipientAccountNumber;

    private Long beneficiaryId;

    @NotNull(message = "Transfer amount is required")
    @DecimalMin(value = "1.00", message = "Minimum transfer amount is $1.00")
    @DecimalMax(value = "50000.00", message = "Maximum per-transaction transfer limit is $50,000.00")
    private BigDecimal amount;

    private String transferType = "IMPS"; // IMPS, NEFT, RTGS, INTERNAL

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    public TransferRequest() {
    }

    // Getters and Setters
    public String getRecipientAccountNumber() {
        return recipientAccountNumber;
    }

    public void setRecipientAccountNumber(String recipientAccountNumber) {
        this.recipientAccountNumber = recipientAccountNumber;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
