package org.example.budgethanteraren;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("Should create valid transaction")
    void shouldCreateValidTransaction(){
        LocalDate date = LocalDate.now();
        TransactionCategory category = TransactionCategory.GROCERIES;
        double amount = 249.50;
        TransactionType type = TransactionType.EXPENSE;

        Transaction transaction = new Transaction(date, category, amount, type);

        assertEquals(date, transaction.date());
        assertEquals(category, transaction.category());
        assertEquals(249.50, transaction.amount(), 0.0001);
        assertEquals(type, transaction.type());
    }

    @Test
    @DisplayName("Should accept smallest positive amount")
    void shouldAcceptMinimumPositiveAmount(){
        LocalDate date = LocalDate.now();

        Transaction transaction = new Transaction(date,
                TransactionCategory.OTHER, 0.01, TransactionType.EXPENSE);

        assertEquals(0.01, transaction.amount(), 0.0001);
    }

    @Test
    @DisplayName("Should accept smallest negative amount")
    void shouldAcceptMinimumNegativeAmount(){
        LocalDate date = LocalDate.now();

        Transaction transaction = new Transaction(date,
                TransactionCategory.OTHER, -0.01, TransactionType.INCOME);

        assertEquals(-0.01, transaction.amount(), 0.0001);
    }
}
