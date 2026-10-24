package org.example.budgethanteraren;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
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

        List<Transaction> result = manager.filterByDateIntervalSorted(oct1, oct31, false);
    }
}
