package org.example.budgethanteraren;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sun.tools.jdeprscan.DeprDB.loadFromFile;

public class BudgetManager {
    private final List<Transaction> transactions = new ArrayList<>();
    private final Path filePath;

    public BudgetManager(String fileName){
        this.filePath = Path.of(fileName);
        loadFromFile();
    }

    public void addTransaction(LocalDate date, TransactionCategory category, double amount, TransactionType type){
        Transaction transaction = new Transaction(date, category, amount, type);
        transactions.add(transaction);
        saveToFile();
    }

    public List<Transaction> getAllTransactions(){
        return List.copyOf(transactions);
    }

    public double getTotalByType(TransactionType type){
        return transactions.stream()
                .filter(t -> t.type() == type)
                .mapToDouble(Transaction::amount)
                .sum();
    }

    public double getBalance(){
        return getTotalByType(TransactionType.INCOME) - getTotalByType(TransactionType.EXPENSE);
    }

    public Map<TransactionCategory, Double> getSumPerCategory(){
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::category,
                        Collectors.summingDouble(Transaction::amount)));
    }

    public List<Transaction> filterByTypeSorted(TransactionType type, boolean ascendingDate){
        Comparator<Transaction> comparator = Comparator.comparing(Transaction::date);
        if (!ascendingDate){
            comparator = comparator.reversed();
        }
        return transactions.stream()
                .filter(t -> t.type() == type)
                .sorted(comparator)
                .toList();
    }

    public List<Transaction> filterByDateIntervalSorted(LocalDate startDate, LocalDate endDate, boolean ascendingDate){
        Comparator<Transaction> comparator = Comparator.comparing(Transaction::date);
        if (!ascendingDate){
            comparator = comparator.reversed();
        }
        return transactions.stream()
                .filter(t -> t.date().isBefore(startDate) && t.date().isAfter(endDate))
                .sorted(comparator)
                .toList();
    }

    public final void loadFromFile(){
        try{
            if (Files.notExists(filePath)){
                Files.createFile(filePath);
                IO.println("Created new budget file: " + filePath);
                return;
            }

            List<String> lines = Files.readAllLines(filePath);
            int lineNumber = 0;

            for (String line: lines){
                lineNumber++;
                if (line.isBlank()){
                    continue;
                }
                try{
                    transactions.add(Transaction.fromCsv(line));
                } catch (IllegalArgumentException | DateTimeParseException e){
                    IO.println("Error parsing line " + lineNumber + ": " + e.getMessage());
                }
            }
            IO.println("Loaded " + transactions.size() + " transaction(s)");
        }
        catch (IOException e){
            IO.println("Could not read or create file '" + filePath + "':" + e.getMessage());
            IO.println("Starting with an empty in-memory budget instead.");
        }
    }

    public void saveTofFile(){}

}
