package com.bankingsystem.repository;

import com.bankingsystem.entity.Loan;
import com.bankingsystem.entity.LoanStatus;
import com.bankingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserOrderByAppliedAtDesc(User user);

    Optional<Loan> findByIdAndUser(Long id, User user);

    long countByStatus(LoanStatus status);

    @Query("SELECT SUM(l.approvedAmount) FROM Loan l WHERE l.status = 'APPROVED' OR l.status = 'ACTIVE'")
    BigDecimal sumApprovedLoanAmount();

    @Query("SELECT l FROM Loan l WHERE " +
           "(:status IS NULL OR l.status = :status) AND " +
           "(:search IS NULL OR LOWER(l.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.purpose) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Loan> filterLoansAdmin(@Param("status") LoanStatus status,
                               @Param("search") String search,
                               Pageable pageable);
}
