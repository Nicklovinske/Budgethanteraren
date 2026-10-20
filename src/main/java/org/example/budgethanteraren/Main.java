package org.example.budgethanteraren;

public class Main {
    private static final BudgetManager budgetManager = new BudgetManager("transactions.csv");

    public static void main(String[] args){
        boolean running = true;
        IO.println("=== Welcome to your personal Budget Manager ===");

        while (running){
            printMenu();
            int choice = Integer.parseInt(IO.readln("Choose an option (1-7): "));

            switch (choice){
                case 1 -> handleAddTransaction(TransactionType.INCOME);
                case 2 -> handleAddTransaction(TransactionType.EXPENSE);
                case 3 -> printTransactionList("All Transactions", budgetManager.getAllTransactions());
                case 4 -> handleShowSummary();
                case 5 -> handleFilterByType();
                case 6 -> handleFilterByDateInterval();
                case 7 -> {
                    budgetManager.saveToFile();
                    IO.println("Transactions saved. Goodbye Dave");
                        running = false;
                }
                default -> IO.println("Invalid choice. Please choose an option between 1 and 7.");
            }
        }
    }
}
