package com.bankingsystem.repository;

import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.AccountStatus;
import com.bankingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByCustomer(User customer);

    Optional<Account> findByCustomerEmail(String email);

    boolean existsByAccountNumber(String accountNumber);

    long countByStatus(AccountStatus status);

    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.status = 'ACTIVE'")
    BigDecimal sumTotalActiveBalance();

    @Query("SELECT a FROM Account a WHERE " +
           "LOWER(a.accountNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.customer.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.customer.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Account> searchAccounts(@Param("search") String search, Pageable pageable);
}
