package org.example.budgethanteraren;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

public record Transaction(LocalDate date, TransactionCategory category, double amount, TransactionType type) {

    public Transaction {
        Objects.requireNonNull(type, "Transaction type cannot be null");
        Objects.requireNonNull(category, "Transaction category cannot be null");
        Objects.requireNonNull(date, "Transaction date cannot be null");

        if (amount <= 0)
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
    }


    public static Transaction fromCsv(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid CSV format");
        }

        LocalDate date = LocalDate.parse(parts[0].trim());
        TransactionCategory category = TransactionCategory.valueOf(parts[1].trim().toUpperCase());
        double amount = Double.parseDouble(parts[2].trim());
        TransactionType type = TransactionType.valueOf(parts[3].trim().toUpperCase());

        return new Transaction(date, category, amount, type);
    }

    @Override
    public String toString() {
        String sign = (type == TransactionType.INCOME) ? "+" : "-";
        return String.format("%s, %s, %.2f, %s", date, category, amount, type);
    }
}
