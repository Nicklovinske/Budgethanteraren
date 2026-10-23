package org.example.budgethanteraren;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("Should create valid transaction")
    void shouldCreateValidTransaction() {
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
    void shouldAcceptMinimumPositiveAmount() {
        LocalDate date = LocalDate.now();

        Transaction transaction = new Transaction(date,
                TransactionCategory.OTHER, 0.01, TransactionType.EXPENSE);

        assertEquals(0.01, transaction.amount(), 0.0001);
    }

    @Test
    @DisplayName("Should throw exception for invalid amounts")
    void shouldThrowExceptionForInvalidAmounts() {
        LocalDate date = LocalDate.now();
        TransactionCategory category = TransactionCategory.OTHER;
        TransactionType type = TransactionType.EXPENSE;

        assertThrows(IllegalArgumentException.class, () -> new Transaction(date, category, -0.01, type));
        assertThrows(IllegalArgumentException.class, () -> new Transaction(date, category, 0, type));
    }

    @Test
    @DisplayName("Throws InvalidTransactionException when date, category, or type is null")
    void shouldThrowInvalidTransactionExceptionWhenDateCategoryOrTypeNull() {
        LocalDate date = LocalDate.now();

        assertThrows(InvalidTransactionException.class, () -> new Transaction(null,
                TransactionCategory.UTILITIES, 100.0, TransactionType.EXPENSE));

        assertThrows(InvalidTransactionException.class, () -> new Transaction(date,
                null, 100.0, TransactionType.INCOME));

        assertThrows(InvalidTransactionException.class, () -> new Transaction(date,
                TransactionCategory.OTHER, 100.0, null));
    }

    @Test
    @DisplayName("Records to CSV File and reads back identical")
    void shouldWriteToAndReadFromCSVCorrectly() {
        Transaction original = new Transaction(LocalDate.of(2026, 10, 22), TransactionCategory.OTHER,
                100.10, TransactionType.EXPENSE);

        String csvLine = original.toCsv();
        Transaction parsed = Transaction.fromCsv(csvLine);

        assertEquals("2026-10-22,OTHER,100.10,EXPENSE", csvLine);
        assertEquals(original, parsed);

    }

    @Test
    @DisplayName("Throws exception on incorrect CSV line")
    void shouldThrowExceptionOnIncorrectCSVLine() {
        String wrongColumns= "2026-10-22,FOOD,100.00";
        String badDate = "Bananas,FOOD,100.00,EXPENSE";
        String unknownCategory = "2026-10-22,RED_SHOELACES,100.00,EXPENSE";

        assertThrows(InvalidTransactionException.class, () -> Transaction.fromCsv(null));
        assertThrows(InvalidTransactionException.class, () -> Transaction.fromCsv(""));
        assertThrows(InvalidTransactionException.class, () -> Transaction.fromCsv(wrongColumns));
        assertThrows(InvalidTransactionException.class, () -> Transaction.fromCsv(badDate));
        assertThrows(InvalidTransactionException.class, () -> Transaction.fromCsv(unknownCategory));
    }
}
