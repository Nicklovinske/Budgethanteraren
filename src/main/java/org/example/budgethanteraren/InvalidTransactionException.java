package org.example.budgethanteraren;

public class InvalidTransactionException extends IllegalArgumentException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
