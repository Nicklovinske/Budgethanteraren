package org.example.budgethanteraren;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

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

        TransactionCategory category = (type == TransactionType.INCOME)
                ? TransactionCategory.SALARY
                :selectCategory();
        double amount = readPositiveDouble("Enter amount: ");

        budgetManager.addTransaction(date, category, amount, type);
        IO.println("Transaction added and saved to file.");
    }

    private static void handleShowSummary(){
        double totalIncome = budgetManager.getTotalByType(TransactionType.INCOME);
        double totalExpense = budgetManager.getTotalByType(TransactionType.EXPENSE);
        double balance = budgetManager.getBalance();

        IO.println("\n--- Summary ---");
        IO.println("Total Income: " + totalIncome);
        IO.println("Total Expense: " + totalExpense);
        IO.println("Balance: " + balance);

        Map<TransactionCategory, Double> categoryTotals = budgetManager.getSumPerCategory();
        IO.println("\n--- Total per Category ---");
        if (categoryTotals.isEmpty()){
            IO.println("No transactions found.");
        }else{
            categoryTotals.forEach((category, sum) -> IO.println(category + ": " + sum));
        }
    }

    private static void handleFilterByType(){
        IO.println("\n--- Filter by Type ---");
        IO.println("1. Income");
        IO.println("2. Expense");

        TransactionType selectedType = (readInt("Choice (1-2): ")== 1)
                ? TransactionType.INCOME
                : TransactionType.EXPENSE;

        boolean ascending =  readSortOrder();
        List<Transaction> results = budgetManager.filterByTypeSorted(selectedType, ascending);
        printTransactionList("Filtered by " + selectedType, results);
        }

        private static void handleFilterByDateInterval(){
            IO.println("\n--- Filter by Date Interval ---");
            LocalDate start = readDate("Enter start date (YYYY-MM-DD): ");
            LocalDate end = readDate("Enter end date (YYYY-MM-DD): ");

            if(end.isBefore(start)){
                IO.println("End date cannot be before start date.");
            }

            boolean ascending = readSortOrder();
            List<Transaction> results = budgetManager.filterByDateIntervalSorted(start, end, ascending);
            printTransactionList("Filtered by Date Interval " + start + " to " + end, results);
        }

        private static void printTransactionList(String title, List<Transaction> list){
            IO.println("\n=== " + title + " ===");
            if (list.isEmpty()){
                IO.println("No transactions found.");
                return;
            }
            list.forEach(IO::println);

    }

        private static TransactionCategory selectCategory(){
            TransactionCategory[] categories = TransactionCategory.values();
            IO.println("Select a category: ");
            for(int i = 0; i < categories.length; i++){
                IO.println((i+1) + ". " + categories[i]);
            }
            while(true){
                int choice = readInt("Choice (1-" + categories.length + "): ");
                if(choice >= 1 && choice <= categories.length){
                    return categories[choice-1];
                }
                IO.println("Invalid choice. Please pick a number between 1 and " + categories.length + ".");
            }
        }

        private static boolean readSortOrder(){
            IO.println("Sort order by date: ");
            IO.println("1. Ascending");
            IO.println("2. Descending");
            return readInt("Choice (1-2): ") != 2;
        }

        private static int readInt(String prompt){
            while(true){
                IO.println(prompt);
                String input = IO.readln().trim();
                try{
                    return Integer.parseInt(input);
                }catch(NumberFormatException e){
                    IO.println("Invalid input. Please enter a valid number.");
                }
            }
        }

        private static double readPositiveDouble(String prompt){
        while(true){
            IO.println(prompt);
            String input = IO.readln().trim().replace(',','.');
            try{
                double value = Double.parseDouble(input);
                if(value >= 0 && !Double.isInfinite(value)){
                    return value;
                }
                IO.println("Invalid input. Please enter a positive number.");
            }catch(NumberFormatException e){
                IO.println("Invalid input. Please enter a valid number.");
            }
        }
        }

        private static LocalDate readDate(String prompt){
            while(true){
                IO.println(prompt);
                String input = IO.readln().trim();
                if (input.isBlank())
                    return LocalDate.now();
                try{
                    return LocalDate.parse(input);
                }catch(DateTimeParseException e){
                    IO.println("Invalid input. Please enter a valid date in the format YYYY-MM-DD.");
                }
            }
        }
}



