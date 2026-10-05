package com.bankingsystem.service.impl;

import com.bankingsystem.dto.AdminDashboardStatsDTO;
import com.bankingsystem.entity.AccountStatus;
import com.bankingsystem.entity.CardStatus;
import com.bankingsystem.entity.LoanStatus;
import com.bankingsystem.entity.Role;
import com.bankingsystem.repository.*;
import com.bankingsystem.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final CardRepository cardRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            AccountRepository accountRepository,
                            TransactionRepository transactionRepository,
                            LoanRepository loanRepository,
                            CardRepository cardRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.loanRepository = loanRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardStatsDTO getDashboardStats() {
        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO();

        stats.setTotalCustomers(userRepository.countByRole(Role.ROLE_CUSTOMER));
        stats.setActiveAccounts(accountRepository.countByStatus(AccountStatus.ACTIVE));
        stats.setFrozenAccounts(accountRepository.countByStatus(AccountStatus.FROZEN));

        BigDecimal activeBalance = accountRepository.sumTotalActiveBalance();
        stats.setTotalSystemDeposits(activeBalance != null ? activeBalance : BigDecimal.ZERO);

        BigDecimal fees = transactionRepository.sumTotalFeesCollected();
        stats.setTotalSystemFees(fees != null ? fees : BigDecimal.ZERO);

        stats.setPendingLoansCount(loanRepository.countByStatus(LoanStatus.PENDING));
        stats.setActiveCardsCount(cardRepository.countByStatus(CardStatus.ACTIVE));
        stats.setTotalTransactionsCount(transactionRepository.count());

        return stats;
    }
}
