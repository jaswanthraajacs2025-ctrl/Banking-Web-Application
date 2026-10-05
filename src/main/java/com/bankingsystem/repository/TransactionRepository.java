package com.bankingsystem.repository;

import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.Transaction;
import com.bankingsystem.entity.TransactionStatus;
import com.bankingsystem.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    List<Transaction> findTop10ByAccountOrderByCreatedAtDesc(Account account);

    List<Transaction> findByAccountOrderByCreatedAtDesc(Account account);

    Page<Transaction> findByAccount(Account account, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account = :account AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR t.createdAt <= :endDate) AND " +
           "(:search IS NULL OR LOWER(t.referenceNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Transaction> filterAccountTransactions(@Param("account") Account account,
                                               @Param("type") TransactionType type,
                                               @Param("status") TransactionStatus status,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               @Param("search") String search,
                                               Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account = :account AND " +
           "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR t.createdAt <= :endDate) " +
           "ORDER BY t.createdAt ASC")
    List<Transaction> findForStatement(@Param("account") Account account,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type AND t.status = 'COMPLETED'")
    BigDecimal sumAmountByType(@Param("type") TransactionType type);

    @Query("SELECT SUM(t.fee) FROM Transaction t WHERE t.status = 'COMPLETED'")
    BigDecimal sumTotalFeesCollected();

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account = :account AND t.status = 'COMPLETED' AND " +
           "(t.type = 'DEPOSIT' OR (t.type = 'TRANSFER' AND t.receiverAccount = :accNumber) OR t.type = 'REFUND')")
    BigDecimal sumIncomeForAccount(@Param("account") Account account, @Param("accNumber") String accNumber);

    @Query("SELECT SUM(t.totalAmount) FROM Transaction t WHERE t.account = :account AND t.status = 'COMPLETED' AND " +
           "(t.type = 'WITHDRAWAL' OR (t.type = 'TRANSFER' AND t.senderAccount = :accNumber) OR t.type = 'BILL_PAYMENT' OR t.type = 'CARD_PAYMENT')")
    BigDecimal sumExpensesForAccount(@Param("account") Account account, @Param("accNumber") String accNumber);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR t.createdAt <= :endDate) AND " +
           "(:search IS NULL OR LOWER(t.referenceNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.account.accountNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Transaction> filterAllTransactionsAdmin(@Param("type") TransactionType type,
                                                @Param("status") TransactionStatus status,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate,
                                                @Param("search") String search,
                                                Pageable pageable);
}
