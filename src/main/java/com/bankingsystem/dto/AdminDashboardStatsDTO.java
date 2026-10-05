package com.bankingsystem.dto;

import java.math.BigDecimal;

public class AdminDashboardStatsDTO {

    private long totalCustomers;
    private long activeAccounts;
    private long frozenAccounts;
    private BigDecimal totalDepositsVolume = BigDecimal.ZERO;
    private BigDecimal totalWithdrawalsVolume = BigDecimal.ZERO;
    private BigDecimal totalTransfersVolume = BigDecimal.ZERO;
    private BigDecimal totalSystemFees = BigDecimal.ZERO;
    private BigDecimal totalSystemDeposits = BigDecimal.ZERO;
    private long pendingLoansCount;
    private long activeCardsCount;
    private long totalTransactionsCount;

    public AdminDashboardStatsDTO() {
    }

    // Getters and Setters
    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getActiveAccounts() {
        return activeAccounts;
    }

    public void setActiveAccounts(long activeAccounts) {
        this.activeAccounts = activeAccounts;
    }

    public long getFrozenAccounts() {
        return frozenAccounts;
    }

    public void setFrozenAccounts(long frozenAccounts) {
        this.frozenAccounts = frozenAccounts;
    }

    public BigDecimal getTotalDepositsVolume() {
        return totalDepositsVolume;
    }

    public void setTotalDepositsVolume(BigDecimal totalDepositsVolume) {
        this.totalDepositsVolume = totalDepositsVolume;
    }

    public BigDecimal getTotalWithdrawalsVolume() {
        return totalWithdrawalsVolume;
    }

    public void setTotalWithdrawalsVolume(BigDecimal totalWithdrawalsVolume) {
        this.totalWithdrawalsVolume = totalWithdrawalsVolume;
    }

    public BigDecimal getTotalTransfersVolume() {
        return totalTransfersVolume;
    }

    public void setTotalTransfersVolume(BigDecimal totalTransfersVolume) {
        this.totalTransfersVolume = totalTransfersVolume;
    }

    public BigDecimal getTotalSystemFees() {
        return totalSystemFees;
    }

    public void setTotalSystemFees(BigDecimal totalSystemFees) {
        this.totalSystemFees = totalSystemFees;
    }

    public BigDecimal getTotalSystemDeposits() {
        return totalSystemDeposits;
    }

    public void setTotalSystemDeposits(BigDecimal totalSystemDeposits) {
        this.totalSystemDeposits = totalSystemDeposits;
    }

    public long getPendingLoansCount() {
        return pendingLoansCount;
    }

    public void setPendingLoansCount(long pendingLoansCount) {
        this.pendingLoansCount = pendingLoansCount;
    }

    public long getActiveCardsCount() {
        return activeCardsCount;
    }

    public void setActiveCardsCount(long activeCardsCount) {
        this.activeCardsCount = activeCardsCount;
    }

    public long getTotalTransactionsCount() {
        return totalTransactionsCount;
    }

    public void setTotalTransactionsCount(long totalTransactionsCount) {
        this.totalTransactionsCount = totalTransactionsCount;
    }
}
