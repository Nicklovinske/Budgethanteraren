package org.example.budgethanteraren;

import java.time.LocalDate;

public class Main {
    private static final BudgetManager budgetManager = new BudgetManager("transactions.csv");

    public static void main(String[] args){
        boolean running = true;
        IO.println("=== Welcome to your personal Budget Manager ===");

        while (running){
            printMenu();
            int choice = Integer.parseInt(IO.readln("Choose an option (1-8): "));

            switch (choice){
                case 1 -> handleAddTransaction(TransactionType.INCOME);
                case 2 -> handleAddTransaction(TransactionType.EXPENSE);
                case 3 -> printTransactionList("All Transactions", budgetManager.getAllTransactions());
                case 4 -> handleShowSummary();
                case 5 -> handleFilterByType();
                case 6 -> handleFilterByDateInterval();
                case 7 -> IO.println("I'm sorry Dave, I'm afraid I can't do that.");
                case 8 -> {
                    budgetManager.saveToFile();
                    IO.println("Transactions saved. Goodbye Dave");
                        running = false;


                }
                default -> IO.println("Invalid choice. Please choose an option between 1 and 8.");
            }
        }
    }

    private static void printMenu(){
        IO.println("=== Menu ===");
        IO.println("1. Add Income");
        IO.println("2. Add Expense");
        IO.println("3. Show All Transactions");
        IO.println("4. Show Summary");
        IO.println("5. Filter by Type");
        IO.println("6. Filter by Date Interval");
        IO.println("7. Open the airlock doors");
        IO.println("8. Exit without saving");
    }

    private static void handleAddTransaction(TransactionType type){
        IO.println("\n--- Add " + type + " ---");
        LocalDate date = readDate("Enter date (YYYY-MM-DD) or press Enter for today: ");
        TransactionCategory category = selectCategory();
        double amount = readPositiveDouble("Enter amount: ");

        budgetManager.addTransaction(date, category, amount, type);
        IO.println("Transaction added and saved to file.");
    }

    private static void handleShowSummary(){
        double totalIncome = budgetManager.getTotalIncome();
        double totalExpense = budgetManager.getTotalExpense();
        double balance = budgetManager.getBalance();

        IO.println("\n--- Summary ---");
        IO.println("Total Income: " + totalIncome);
        IO.println("Total Expense: " + totalExpense);
        IO.println("Balance: " + balance);
    }
}
