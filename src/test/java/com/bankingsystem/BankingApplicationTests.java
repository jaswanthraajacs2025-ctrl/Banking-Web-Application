package com.bankingsystem;

import com.bankingsystem.dto.FeeCalculationDTO;
import com.bankingsystem.service.LoanService;
import com.bankingsystem.service.SystemSettingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class BankingApplicationTests {

    @Autowired
    private SystemSettingService systemSettingService;

    @Autowired
    private LoanService loanService;

    @Test
    void contextLoads() {
        assertNotNull(systemSettingService);
        assertNotNull(loanService);
    }

    @Test
    void testFeeAndTaxCalculation() {
        BigDecimal amount = new BigDecimal("1000.00");
        FeeCalculationDTO calc = systemSettingService.calculateFeeAndTax(amount);

        assertNotNull(calc);
        assertEquals(new BigDecimal("1000.00"), calc.getAmount());
        assertTrue(calc.getServiceFee().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(calc.getTax().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(calc.getTotalDebit().compareTo(amount) >= 0);
    }

    @Test
    void testLoanEmiCalculation() {
        BigDecimal principal = new BigDecimal("10000.00");
        double interestRate = 10.0;
        int tenureMonths = 12;

        BigDecimal emi = loanService.calculateEmi(principal, interestRate, tenureMonths);

        assertNotNull(emi);
        assertTrue(emi.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(emi.compareTo(new BigDecimal("800.00")) > 0);
        assertTrue(emi.compareTo(new BigDecimal("1000.00")) < 0);
    }
}
