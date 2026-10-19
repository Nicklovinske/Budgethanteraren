package org.example.budgethanteraren;

import java.time.LocalDate;
import java.util.Objects;

public record Transaction(LocalDate date, TransactionCategory transactionCategory, double amount, TransactionType type) {

    public Transaction {
        Objects.requireNonNull(type, "Transaction type cannot be null");
        Objects.requireNonNull(transactionCategory, "Transaction category cannot be null");
        Objects.requireNonNull(amount, "Transaction amount cannot be null");
        Objects.requireNonNull(date, "Transaction date cannot be null");

        if (amount <= 0)
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
    }

    @Override
    public String toString() {
        String sign = type == TransactionType.INCOME ? "+" : "-";
        return String.format("%s %s %s %s", date, transactionCategory, sign, amount);
    }
}
