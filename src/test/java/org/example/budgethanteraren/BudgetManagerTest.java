package org.example.budgethanteraren;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetManagerTest {

    @TempDir
    Path tempDir;

    private Path tempCsvFile;
    private BudgetManager manager;

    @BeforeEach
    void setUp() {
        tempCsvFile = tempDir.resolve("test_transactions.csv");
        manager = new BudgetManager(tempCsvFile.toString());
    }

    @Test
    @DisplayName("Accurately calculates total income, expense and net balance")
    void shouldCalculateTotalIncomeExpenseAndNetBalance() {
        LocalDate date = LocalDate.of(2026, 10, 22);
        manager.addTransaction(date, TransactionCategory.SALARY, 30000.00,TransactionType.INCOME);
        manager.addTransaction(date, TransactionCategory.HOUSING, 6900.00,TransactionType.EXPENSE);
        manager.addTransaction(date, TransactionCategory.GROCERIES, 1000.00,TransactionType.EXPENSE);

        double totalIncome = manager.getTotalByType(TransactionType.INCOME);
        double totalExpense = manager.getTotalByType(TransactionType.EXPENSE);
        double balance = manager.getBalance();

        assertEquals(30000.00, totalIncome);
        assertEquals(7900.00, totalExpense);
        assertEquals(22100.00, balance);
    }

    @Test
    @DisplayName("Empy transaction list returns 0 balance and empty maps/lists")
    void shouldHandleEmptryTransactionList(){
        double balance = manager.getBalance();
        double income = manager.getTotalByType(TransactionType.INCOME);

        Map<TransactionCategory, Double> categorySums = manager.getSumPerCategory();
        List<Transaction> allTransactions = manager.getAllTransactions();

        assertEquals(0.0, balance, 0.001);
        assertEquals(0.0, income, 0.001);
        assertTrue(categorySums.isEmpty());
        assertTrue(allTransactions.isEmpty());
    }

    @Test
    @DisplayName("Stream groups and sums amounts per category accurately")
    void shouldGroupAndSumPerCategory(){
        LocalDate date = LocalDate.of(2026,10,23);
        manager.addTransaction(date, TransactionCategory.GROCERIES, 120.50, TransactionType.EXPENSE);
        manager.addTransaction(date, TransactionCategory.GROCERIES, 279.50, TransactionType.EXPENSE);
        manager.addTransaction(date, TransactionCategory.UTILITIES, 500.00, TransactionType.EXPENSE);

        Map<TransactionCategory, Double> categorySums = manager.getSumPerCategory();

        assertEquals(2, categorySums.size());
        assertEquals(400.00, categorySums.get(TransactionCategory.GROCERIES), 0.001);
        assertEquals(500.00, categorySums.get(TransactionCategory.UTILITIES), 0.001);
        assertNull(categorySums.get(TransactionCategory.ENTERTAINMENT));
    }

    @Test
    @DisplayName("Filters by date interval (including boundary dates) and sorts")
    void shouldFilterByDateIntervalInclusiveAndSortDescending(){
        LocalDate oct1 = LocalDate.of(2026, 10, 1);
        LocalDate oct15 = LocalDate.of(2026, 10, 15);
        LocalDate oct31 = LocalDate.of(2026, 10, 31);
        LocalDate nov1 = LocalDate.of(2026, 11, 1);

        manager.addTransaction(oct1, TransactionCategory.GROCERIES, 100.00, TransactionType.EXPENSE);
        manager.addTransaction(oct15, TransactionCategory.GROCERIES, 200.00, TransactionType.EXPENSE);
        manager.addTransaction(oct31, TransactionCategory.GROCERIES, 300.00, TransactionType.EXPENSE);
        manager.addTransaction(nov1, TransactionCategory.GROCERIES, 400.00, TransactionType.EXPENSE);

        List<Transaction> result = manager.filterByDateIntervalSorted(oct1, oct31, false);

        assertEquals(3, result.size(), "Both boundary date (Oct 1, and Oct 31) must be included.");
        assertEquals(oct31, result.get(0).date());
        assertEquals(oct15, result.get(1).date());
        assertEquals(oct1, result.get(2).date());
    }

    @Test
    @DisplayName("Throws InvalidTransactionException when end date is before start date or null")
    void shouldThrowInvalidTransactionExceptionForInvalidDateInterval(){
        LocalDate start = LocalDate.of(2026,10,20);
        LocalDate endBeforeStart = LocalDate.of(2026,10,10);

        assertThrows(InvalidTransactionException.class, () ->
                manager.filterByDateIntervalSorted(start, endBeforeStart, true));
        assertThrows(InvalidTransactionException.class, () ->
                manager.filterByDateIntervalSorted(null, start, true));
        assertThrows(InvalidTransactionException.class, () ->
                manager.filterByTypeSorted(null,true));

    }

    @Test
    @DisplayName("Persists transactions to CSV and relaods them while skipping corrupt lines")
    void shouldSaveAndReloadTransactionsAndSkipCorruptedLines() throws IOException {
        Path customCsv = tempDir.resolve("corrupted_and_valid.csv");
        List<String> lines = List.of(
                "2026-10-01,SALARY,25000.00,INCOME",
                "corrupted-date,FOOD,-100,EXPENSE",
                "broken_line_without_commas",
                "2026-10-05,FOOD,300.00,EXPENSE");

        Files.write(customCsv, lines);
        BudgetManager reloadedManager = new BudgetManager(customCsv.toString());
        List<Transaction> loaded = reloadedManager.getAllTransactions();

        assertEquals(2, loaded.size(), "Should load the 2 valid rows and skip the corrupted ones");
        assertEquals(24700.00, reloadedManager.getBalance(), 0.001);
    }
}
