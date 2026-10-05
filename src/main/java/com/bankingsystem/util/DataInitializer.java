package com.bankingsystem.util;

import com.bankingsystem.entity.*;
import com.bankingsystem.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final CardRepository cardRepository;
    private final LoanRepository loanRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           BeneficiaryRepository beneficiaryRepository,
                           CardRepository cardRepository,
                           LoanRepository loanRepository,
                           NotificationRepository notificationRepository,
                           AuditLogRepository auditLogRepository,
                           SystemSettingRepository systemSettingRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.cardRepository = cardRepository;
        this.loanRepository = loanRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogRepository = auditLogRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initSystemSettings();
        if (userRepository.count() == 0) {
            initDemoData();
        }
    }

    private void initSystemSettings() {
        createSettingIfMissing("TRANSFER_FEE_PERCENT", "0.25", "Standard transfer service fee percentage (%)", "CHARGES");
        createSettingIfMissing("SERVICE_TAX_PERCENT", "0.10", "Applicable banking transaction service tax (%)", "CHARGES");
        createSettingIfMissing("SAVINGS_INTEREST_RATE", "4.5", "Annual percentage rate (APR) for Savings Accounts", "RATES");
        createSettingIfMissing("DAILY_TRANSFER_LIMIT", "50000.00", "Default maximum daily customer transfer threshold ($)", "LIMITS");
        createSettingIfMissing("MIN_ACCOUNT_BALANCE", "50.00", "Minimum recommended maintaining balance ($)", "LIMITS");
        createSettingIfMissing("SUPPORT_HOTLINE", "+1 (800) 555-APEX", "24/7 Priority Banking Customer Support Phone", "CONTACT");
    }

    private void createSettingIfMissing(String key, String value, String desc, String category) {
        if (!systemSettingRepository.existsBySettingKey(key)) {
            SystemSetting setting = new SystemSetting(key, value, desc, category);
            systemSettingRepository.save(setting);
        }
    }

    private void initDemoData() {
        System.out.println(">>> Initializing Apex Horizon Banking Demo Data...");

        // 1. Admin User
        User admin = new User();
        admin.setFullName("System Administrator");
        admin.setEmail("admin@bankdemo.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setMobileNumber("+1 (800) 555-0199");
        admin.setDateOfBirth(LocalDate.of(1988, 5, 15));
        admin.setAddress("Apex Tower 1, Financial District, New York, NY 10005");
        admin.setCustomerId("ADMIN001");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        auditLogRepository.save(new AuditLog("admin@bankdemo.com", AuditAction.REGISTER, "Initial system super admin account provisioned", "127.0.0.1", "SUCCESS"));

        // 2. Primary Customer (Alexander Wright)
        User customer1 = new User();
        customer1.setFullName("Alexander Wright");
        customer1.setEmail("customer@bankdemo.com");
        customer1.setPassword(passwordEncoder.encode("Customer@123"));
        customer1.setMobileNumber("+1 (555) 234-5678");
        customer1.setDateOfBirth(LocalDate.of(1992, 8, 20));
        customer1.setAddress("742 Evergreen Terrace, Suite 4B, Boston, MA 02134");
        customer1.setCustomerId("CUST100881");
        customer1.setRole(Role.ROLE_CUSTOMER);
        customer1.setEnabled(true);

        Account account1 = new Account();
        account1.setAccountNumber("ACC1000889901");
        account1.setCustomer(customer1);
        account1.setAccountType(AccountType.SAVINGS);
        account1.setBalance(new BigDecimal("25450.00"));
        account1.setAvailableBalance(new BigDecimal("25450.00"));
        account1.setCurrency("USD");
        account1.setStatus(AccountStatus.ACTIVE);
        account1.setDailyTransferLimit(new BigDecimal("50000.00"));
        account1.setBranchName("Apex Horizon Boston Metro Branch");
        account1.setIfscCode("APEX0008822");
        customer1.setAccount(account1);

        userRepository.save(customer1);

        // 3. Customer 2 (Sarah Jenkins)
        User customer2 = new User();
        customer2.setFullName("Sarah Jenkins");
        customer2.setEmail("sarah.jenkins@bankdemo.com");
        customer2.setPassword(passwordEncoder.encode("Customer@123"));
        customer2.setMobileNumber("+1 (555) 876-5432");
        customer2.setDateOfBirth(LocalDate.of(1995, 3, 11));
        customer2.setAddress("1200 Market Street, San Francisco, CA 94102");
        customer2.setCustomerId("CUST100882");
        customer2.setRole(Role.ROLE_CUSTOMER);
        customer2.setEnabled(true);

        Account account2 = new Account();
        account2.setAccountNumber("ACC1000889902");
        account2.setCustomer(customer2);
        account2.setAccountType(AccountType.SAVINGS);
        account2.setBalance(new BigDecimal("14800.00"));
        account2.setAvailableBalance(new BigDecimal("14800.00"));
        account2.setCurrency("USD");
        account2.setStatus(AccountStatus.ACTIVE);
        account2.setDailyTransferLimit(new BigDecimal("35000.00"));
        account2.setBranchName("Apex Horizon West Coast Hub");
        account2.setIfscCode("APEX0009944");
        customer2.setAccount(account2);

        userRepository.save(customer2);

        // 4. Customer 3 (Elena Rostova)
        User customer3 = new User();
        customer3.setFullName("Elena Rostova");
        customer3.setEmail("elena.rostova@bankdemo.com");
        customer3.setPassword(passwordEncoder.encode("Customer@123"));
        customer3.setMobileNumber("+1 (555) 432-1098");
        customer3.setDateOfBirth(LocalDate.of(1990, 11, 28));
        customer3.setAddress("500 Michigan Ave, Chicago, IL 60611");
        customer3.setCustomerId("CUST100883");
        customer3.setRole(Role.ROLE_CUSTOMER);
        customer3.setEnabled(true);

        Account account3 = new Account();
        account3.setAccountNumber("ACC1000889903");
        account3.setCustomer(customer3);
        account3.setAccountType(AccountType.CURRENT);
        account3.setBalance(new BigDecimal("9350.00"));
        account3.setAvailableBalance(new BigDecimal("9350.00"));
        account3.setCurrency("USD");
        account3.setStatus(AccountStatus.ACTIVE);
        account3.setDailyTransferLimit(new BigDecimal("40000.00"));
        account3.setBranchName("Apex Horizon Midwest Centre");
        account3.setIfscCode("APEX0007711");
        customer3.setAccount(account3);

        userRepository.save(customer3);

        // 5. Beneficiaries for Alexander Wright
        Beneficiary b1 = new Beneficiary();
        b1.setUser(customer1);
        b1.setName("Sarah Jenkins");
        b1.setAccountNumber("ACC1000889902");
        b1.setBankName("Apex Horizon Bank");
        b1.setIfsc("APEX0009944");
        b1.setNickname("Sarah (Colleague)");
        beneficiaryRepository.save(b1);

        Beneficiary b2 = new Beneficiary();
        b2.setUser(customer1);
        b2.setName("Elena Rostova");
        b2.setAccountNumber("ACC1000889903");
        b2.setBankName("Apex Horizon Bank");
        b2.setIfsc("APEX0007711");
        b2.setNickname("Elena Design");
        beneficiaryRepository.save(b2);

        // 6. Cards for Alexander Wright
        Card card1 = new Card();
        card1.setUser(customer1);
        card1.setAccount(account1);
        card1.setCardType(CardType.DEBIT);
        card1.setCardNumberMasked("4532 •••• •••• 8812");
        card1.setCardHolderName("ALEXANDER WRIGHT");
        card1.setExpiryDate("08/29");
        card1.setStatus(CardStatus.ACTIVE);
        card1.setCardNetwork("VISA Platinum");
        card1.setSpendingLimit(new BigDecimal("8000.00"));
        card1.setDailyLimit(new BigDecimal("3000.00"));
        cardRepository.save(card1);

        Card card2 = new Card();
        card2.setUser(customer1);
        card2.setAccount(account1);
        card2.setCardType(CardType.CREDIT);
        card2.setCardNumberMasked("5412 •••• •••• 4920");
        card2.setCardHolderName("ALEXANDER WRIGHT");
        card2.setExpiryDate("11/28");
        card2.setStatus(CardStatus.ACTIVE);
        card2.setCardNetwork("Mastercard World Elite");
        card2.setSpendingLimit(new BigDecimal("15000.00"));
        card2.setDailyLimit(new BigDecimal("5000.00"));
        cardRepository.save(card2);

        // 7. Loans for Alexander Wright
        Loan loan1 = new Loan();
        loan1.setUser(customer1);
        loan1.setLoanType(LoanType.PERSONAL_LOAN);
        loan1.setRequestedAmount(new BigDecimal("15000.00"));
        loan1.setApprovedAmount(new BigDecimal("15000.00"));
        loan1.setInterestRate(10.5);
        loan1.setTenureMonths(24);
        loan1.setMonthlyIncome(new BigDecimal("7500.00"));
        loan1.setEmploymentType("Salaried - Tech Lead");
        loan1.setPurpose("Home renovation and tech workspace upgrade");
        loan1.setStatus(LoanStatus.APPROVED);
        loan1.setMonthlyEmi(new BigDecimal("695.78"));
        loan1.setTotalPayable(new BigDecimal("16698.72"));
        loan1.setReviewedAt(LocalDateTime.now().minusDays(10));
        loanRepository.save(loan1);

        Loan loan2 = new Loan();
        loan2.setUser(customer1);
        loan2.setLoanType(LoanType.VEHICLE_LOAN);
        loan2.setRequestedAmount(new BigDecimal("35000.00"));
        loan2.setInterestRate(9.0);
        loan2.setTenureMonths(48);
        loan2.setMonthlyIncome(new BigDecimal("7500.00"));
        loan2.setEmploymentType("Salaried - Tech Lead");
        loan2.setPurpose("Electric vehicle purchase (Tesla Model Y)");
        loan2.setStatus(LoanStatus.PENDING);
        loan2.setMonthlyEmi(new BigDecimal("870.76"));
        loan2.setTotalPayable(new BigDecimal("41796.48"));
        loanRepository.save(loan2);

        // 8. Sample Transactions for Alexander Wright
        createTx(account1, "TXN202608101001", TransactionType.DEPOSIT, new BigDecimal("10000.00"), "Initial Account Funding Deposit", "EXTERNAL_WIRE", account1.getAccountNumber(), new BigDecimal("10000.00"), LocalDateTime.now().minusDays(20));
        createTx(account1, "TXN202608121002", TransactionType.DEPOSIT, new BigDecimal("8500.00"), "Monthly Corporate Salary Credit", "TechCorp Global Payroll", account1.getAccountNumber(), new BigDecimal("18500.00"), LocalDateTime.now().minusDays(15));
        createTx(account1, "TXN202608141003", TransactionType.TRANSFER, new BigDecimal("1200.00"), "Rent Split & Utilities", account1.getAccountNumber(), account2.getAccountNumber(), new BigDecimal("17295.80"), LocalDateTime.now().minusDays(12));
        createTx(account1, "TXN202608161004", TransactionType.LOAN, new BigDecimal("15000.00"), "Personal Loan Disbursement (#1)", "Apex Loan Desk", account1.getAccountNumber(), new BigDecimal("32295.80"), LocalDateTime.now().minusDays(10));
        createTx(account1, "TXN202608181005", TransactionType.WITHDRAWAL, new BigDecimal("800.00"), "ATM Cash Withdrawal - Downtown Center", account1.getAccountNumber(), "ATM-NY-092", new BigDecimal("31495.80"), LocalDateTime.now().minusDays(8));
        createTx(account1, "TXN202608201006", TransactionType.TRANSFER, new BigDecimal("6000.00"), "Investment Portfolio Contribution", account1.getAccountNumber(), "ACC9988112200", new BigDecimal("25480.80"), LocalDateTime.now().minusDays(5));
        createTx(account1, "TXN202608221007", TransactionType.BILL_PAYMENT, new BigDecimal("30.80"), "Monthly Cloud Hosting & Subscriptions", account1.getAccountNumber(), "CloudServices Inc", new BigDecimal("25450.00"), LocalDateTime.now().minusDays(2));

        // 9. Notifications for Alexander Wright
        notificationRepository.save(new Notification(customer1, "Welcome to Apex Horizon Bank", "Your premium checking and savings account ACC1000889901 is now active.", "SUCCESS", "/customer/dashboard"));
        notificationRepository.save(new Notification(customer1, "Loan Approved", "Your Personal Loan application #1 for $15,000.00 has been approved and credited.", "SUCCESS", "/customer/loans"));
        notificationRepository.save(new Notification(customer1, "Card Shipped", "Your VISA Platinum debit card 4532 •••• •••• 8812 is active and ready for online use.", "INFO", "/customer/cards"));

        // 10. Audit Logs
        auditLogRepository.save(new AuditLog("customer@bankdemo.com", AuditAction.LOGIN, "Customer signed into Web Banking Portal", "192.168.1.10", "SUCCESS"));
        auditLogRepository.save(new AuditLog("customer@bankdemo.com", AuditAction.TRANSFER, "Transferred $1,200.00 to Sarah Jenkins (ACC1000889902)", "192.168.1.10", "SUCCESS"));
        auditLogRepository.save(new AuditLog("admin@bankdemo.com", AuditAction.LOAN_APPROVED, "Admin approved Loan #1 for Alexander Wright ($15,000.00)", "127.0.0.1", "SUCCESS"));

        System.out.println(">>> Demo Data Initialized Successfully!");
        System.out.println(">>> Admin Login: admin@bankdemo.com / Admin@123");
        System.out.println(">>> Customer Login: customer@bankdemo.com / Customer@123");
    }

    private void createTx(Account account, String ref, TransactionType type, BigDecimal amount, String desc, String sender, String receiver, BigDecimal balanceAfter, LocalDateTime date) {
        Transaction tx = new Transaction();
        tx.setReferenceNumber(ref);
        tx.setAccount(account);
        tx.setType(type);
        tx.setAmount(amount);
        tx.setFee(BigDecimal.ZERO);
        tx.setTax(BigDecimal.ZERO);
        tx.setTotalAmount(amount);
        tx.setDescription(desc);
        tx.setSenderAccount(sender);
        tx.setReceiverAccount(receiver);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setBalanceAfter(balanceAfter);
        transactionRepository.save(tx);
    }
}
