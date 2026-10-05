package com.bankingsystem.service;

import com.bankingsystem.dto.BeneficiaryRequest;
import com.bankingsystem.entity.Beneficiary;
import com.bankingsystem.entity.User;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryService {
    Beneficiary addBeneficiary(User user, BeneficiaryRequest request, String ipAddress);
    Beneficiary updateBeneficiary(User user, Long id, BeneficiaryRequest request, String ipAddress);
    void deleteBeneficiary(User user, Long id, String ipAddress);
    List<Beneficiary> getBeneficiariesForUser(User user);
    Optional<Beneficiary> getBeneficiaryByIdAndUser(Long id, User user);
}
