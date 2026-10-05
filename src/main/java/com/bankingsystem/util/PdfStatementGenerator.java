package com.bankingsystem.util;

import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.Transaction;
import com.bankingsystem.entity.User;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfStatementGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private static final DateTimeFormatter PERIOD_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public static byte[] generateStatement(User user, Account account, List<Transaction> transactions, LocalDate startDate, LocalDate endDate) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Colors
            Color primaryColor = new Color(20, 45, 95); // Deep Navy
            Color headerBg = new Color(240, 244, 248);
            Color altRowBg = new Color(248, 249, 250);

            // Fonts
            Font bankTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor);
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, primaryColor);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);

            // 1. Header Table (Bank Info & Statement Title)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{60, 40});

            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.addElement(new Paragraph("APEX HORIZON BANK", bankTitleFont));
            leftCell.addElement(new Paragraph("Digital Financial Services • Global Banking", smallFont));
            leftCell.addElement(new Paragraph("100 Horizon Tower, Wall Street, NY 10005", smallFont));
            leftCell.addElement(new Paragraph("Support: support@apexhorizonbank.com | +1 (800) 555-APEX", smallFont));
            headerTable.addCell(leftCell);

            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph stmtTitle = new Paragraph("ACCOUNT STATEMENT", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryColor));
            stmtTitle.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(stmtTitle);
            
            String periodStr = (startDate != null ? startDate.format(PERIOD_FORMAT) : "All Time") +
                    " to " + (endDate != null ? endDate.format(PERIOD_FORMAT) : "Present");
            Paragraph period = new Paragraph("Period: " + periodStr, normalFont);
            period.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(period);

            Paragraph genDate = new Paragraph("Generated: " + LocalDateTime.now().format(DATE_FORMAT), smallFont);
            genDate.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(genDate);

            headerTable.addCell(rightCell);
            document.add(headerTable);

            document.add(new Paragraph(" "));

            // 2. Customer & Account Details Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{50, 50});

            PdfPCell custInfoCell = new PdfPCell();
            custInfoCell.setBackgroundColor(headerBg);
            custInfoCell.setPadding(8);
            custInfoCell.addElement(new Paragraph("ACCOUNT HOLDER DETAILS", sectionTitleFont));
            custInfoCell.addElement(new Paragraph("Name: " + user.getFullName(), boldFont));
            custInfoCell.addElement(new Paragraph("Customer ID: " + user.getCustomerId(), normalFont));
            custInfoCell.addElement(new Paragraph("Email: " + user.getEmail(), normalFont));
            custInfoCell.addElement(new Paragraph("Address: " + (user.getAddress() != null ? user.getAddress() : "N/A"), normalFont));
            infoTable.addCell(custInfoCell);

            PdfPCell accInfoCell = new PdfPCell();
            accInfoCell.setBackgroundColor(headerBg);
            accInfoCell.setPadding(8);
            accInfoCell.addElement(new Paragraph("ACCOUNT SUMMARY", sectionTitleFont));
            accInfoCell.addElement(new Paragraph("Account Number: " + account.getAccountNumber(), boldFont));
            accInfoCell.addElement(new Paragraph("Account Type: " + account.getAccountType() + " | Status: " + account.getStatus(), normalFont));
            accInfoCell.addElement(new Paragraph("Branch / IFSC: " + account.getBranchName() + " (" + account.getIfscCode() + ")", normalFont));
            accInfoCell.addElement(new Paragraph("Current Balance: $" + account.getBalance() + " " + account.getCurrency(), boldFont));
            infoTable.addCell(accInfoCell);

            document.add(infoTable);
            document.add(new Paragraph(" "));

            // 3. Transactions Table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{18, 18, 12, 28, 12, 12});

            String[] headers = {"Date & Time", "Reference #", "Type", "Description", "Amount ($)", "Balance ($)"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE)));
                cell.setBackgroundColor(primaryColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            boolean alt = false;
            BigDecimal totalCredits = BigDecimal.ZERO;
            BigDecimal totalDebits = BigDecimal.ZERO;

            for (Transaction tx : transactions) {
                Color rowColor = alt ? altRowBg : Color.WHITE;
                alt = !alt;

                boolean isCredit = tx.getType().name().equals("DEPOSIT") || 
                                   (tx.getType().name().equals("TRANSFER") && tx.getReceiverAccount() != null && tx.getReceiverAccount().equals(account.getAccountNumber())) ||
                                   tx.getType().name().equals("REFUND") || tx.getType().name().equals("LOAN");

                if (isCredit) {
                    totalCredits = totalCredits.add(tx.getAmount());
                } else {
                    totalDebits = totalDebits.add(tx.getTotalAmount() != null ? tx.getTotalAmount() : tx.getAmount());
                }

                // Date
                PdfPCell c1 = new PdfPCell(new Phrase(tx.getCreatedAt().format(DATE_FORMAT), smallFont));
                c1.setBackgroundColor(rowColor);
                c1.setPadding(4);
                table.addCell(c1);

                // Ref
                PdfPCell c2 = new PdfPCell(new Phrase(tx.getReferenceNumber(), smallFont));
                c2.setBackgroundColor(rowColor);
                c2.setPadding(4);
                table.addCell(c2);

                // Type
                PdfPCell c3 = new PdfPCell(new Phrase(tx.getType().name(), smallFont));
                c3.setBackgroundColor(rowColor);
                c3.setPadding(4);
                table.addCell(c3);

                // Description
                PdfPCell c4 = new PdfPCell(new Phrase(tx.getDescription() != null ? tx.getDescription() : "", smallFont));
                c4.setBackgroundColor(rowColor);
                c4.setPadding(4);
                table.addCell(c4);

                // Amount
                String amtPrefix = isCredit ? "+$" : "-$";
                Color amtColor = isCredit ? new Color(0, 128, 0) : new Color(178, 34, 34);
                PdfPCell c5 = new PdfPCell(new Phrase(amtPrefix + tx.getAmount(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, amtColor)));
                c5.setBackgroundColor(rowColor);
                c5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c5.setPadding(4);
                table.addCell(c5);

                // Balance After
                PdfPCell c6 = new PdfPCell(new Phrase("$" + (tx.getBalanceAfter() != null ? tx.getBalanceAfter() : "-"), smallFont));
                c6.setBackgroundColor(rowColor);
                c6.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c6.setPadding(4);
                table.addCell(c6);
            }

            if (transactions.isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No transactions recorded for the selected period.", normalFont));
                emptyCell.setColspan(6);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                emptyCell.setPadding(10);
                table.addCell(emptyCell);
            }

            document.add(table);

            // 4. Summary & Disclaimer
            document.add(new Paragraph(" "));
            Paragraph disclaimer = new Paragraph(
                    "Note: This is a system-generated financial statement from Apex Horizon Bank demo platform. " +
                    "Total Credits: $" + totalCredits + " | Total Debits: $" + totalDebits + " | Records: " + transactions.size(),
                    smallFont
            );
            disclaimer.setAlignment(Element.ALIGN_CENTER);
            document.add(disclaimer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
