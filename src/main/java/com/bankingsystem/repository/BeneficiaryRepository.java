package com.bankingsystem.repository;

import com.bankingsystem.entity.Beneficiary;
import com.bankingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findByUserOrderByCreatedAtDesc(User user);

    Optional<Beneficiary> findByIdAndUser(Long id, User user);

    boolean existsByUserAndAccountNumber(User user, String accountNumber);
}
