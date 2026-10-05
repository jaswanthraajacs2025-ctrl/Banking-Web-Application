package com.bankingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BeneficiaryRequest {

    private Long id;

    @NotBlank(message = "Beneficiary name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Account number is required")
    @Size(min = 6, max = 30, message = "Account number must be between 6 and 30 characters")
    private String accountNumber;

    @NotBlank(message = "Please confirm the account number")
    private String confirmAccountNumber;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    private String ifsc;

    @Size(max = 50, message = "Nickname cannot exceed 50 characters")
    private String nickname;

    public BeneficiaryRequest() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getConfirmAccountNumber() {
        return confirmAccountNumber;
    }

    public void setConfirmAccountNumber(String confirmAccountNumber) {
        this.confirmAccountNumber = confirmAccountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
