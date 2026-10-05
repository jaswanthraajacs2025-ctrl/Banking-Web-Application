package com.bankingsystem.dto;

import java.math.BigDecimal;

public class FeeCalculationDTO {

    private BigDecimal amount;
    private BigDecimal serviceFee;
    private BigDecimal tax;
    private BigDecimal totalDebit;
    private double feePercentage;
    private double taxPercentage;

    public FeeCalculationDTO() {
    }

    public FeeCalculationDTO(BigDecimal amount, BigDecimal serviceFee, BigDecimal tax, BigDecimal totalDebit, double feePercentage, double taxPercentage) {
        this.amount = amount;
        this.serviceFee = serviceFee;
        this.tax = tax;
        this.totalDebit = totalDebit;
        this.feePercentage = feePercentage;
        this.taxPercentage = taxPercentage;
    }

    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(BigDecimal totalDebit) {
        this.totalDebit = totalDebit;
    }

    public double getFeePercentage() {
        return feePercentage;
    }

    public void setFeePercentage(double feePercentage) {
        this.feePercentage = feePercentage;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }
}
