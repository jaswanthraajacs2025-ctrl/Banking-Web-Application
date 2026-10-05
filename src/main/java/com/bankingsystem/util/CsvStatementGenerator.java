package com.bankingsystem.util;

import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvStatementGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static byte[] generateCsvStatement(Account account, List<Transaction> transactions) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(out, StandardCharsets.UTF_8),
                CSVFormat.DEFAULT.builder().setHeader("Transaction Reference", "Date", "Type", "Sender", "Receiver", "Amount ($)", "Fee ($)", "Tax ($)", "Total ($)", "Description", "Status", "Balance After ($)").build())) {

            for (Transaction tx : transactions) {
                csvPrinter.printRecord(
                        tx.getReferenceNumber(),
                        tx.getCreatedAt().format(DATE_FORMAT),
                        tx.getType().name(),
                        tx.getSenderAccount() != null ? tx.getSenderAccount() : "-",
                        tx.getReceiverAccount() != null ? tx.getReceiverAccount() : "-",
                        tx.getAmount(),
                        tx.getFee() != null ? tx.getFee() : "0.00",
                        tx.getTax() != null ? tx.getTax() : "0.00",
                        tx.getTotalAmount() != null ? tx.getTotalAmount() : tx.getAmount(),
                        tx.getDescription() != null ? tx.getDescription() : "",
                        tx.getStatus().name(),
                        tx.getBalanceAfter() != null ? tx.getBalanceAfter() : "-"
                );
            }
            csvPrinter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
