package com.bankingsystem.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class GeneratorUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter REF_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private GeneratorUtils() {
    }

    public static String generateCustomerId() {
        int number = 100000 + RANDOM.nextInt(900000);
        return "CUST" + number;
    }

    public static String generateAccountNumber() {
        long number = 1000000000L + (long)(RANDOM.nextDouble() * 8999999999L);
        return "ACC" + number;
    }

    public static String generateTransactionReference() {
        String timestamp = LocalDateTime.now().format(REF_FORMATTER);
        int suffix = 1000 + RANDOM.nextInt(9000);
        return "TXN" + timestamp + suffix;
    }

    public static String generateCardNumberMasked() {
        int first4 = 4000 + RANDOM.nextInt(999);
        int last4 = 1000 + RANDOM.nextInt(9000);
        return first4 + " •••• •••• " + last4;
    }

    public static String generateExpiryDate() {
        int month = 1 + RANDOM.nextInt(12);
        int year = (LocalDateTime.now().getYear() % 100) + 4 + RANDOM.nextInt(3);
        return String.format("%02d/%02d", month, year);
    }
}
