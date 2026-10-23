package org.example.budgethanteraren;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;

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
}
