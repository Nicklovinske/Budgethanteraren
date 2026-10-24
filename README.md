
_*Bug 1*_ 
Test `shouldThrowExceptionOnIncorrectCSVLine` failed:

-read the JUnit stack trace, which pointed to `Transaction.fromCsv(Transaction.java:31)` with the message `Caused by: java.lang.IllegalArgumentException: Invalid CSV format: 2026-10-22,GROCERIES,100.00`.
-set breakpoint at fromCsv and stepped through with debugger. Noticed lack of implementation of `InvalidTransactionException`. Also noticed further later of implementation of `InvalidTransactionException`

*Fixed*
-changed line 31 in `Transaction.fromCsv()` to throw `new InvalidTransactionException("Invalid CSV format: " + csvLine)`, and added a null check at the start of `fromCsv()`, wrapped in a try-catch block that throws `InvalidTransactionException`


_*Bug 2*_
Test `shouldFilterByDateIntervalInclusiveAndSortDescending` failed:

-running test failed with `AssertionFailedError: Both boundary date (Oct 1, and Oct 31) must be included. ==> Expected: 3, Actual: 0`
-placed a breakpoint inside `filterByDateIntervalSorted()` and ran debugger
-discovered error in boolean condition in Stream missing the `!` operator in multiple places. Causing the check to be simultaneously for before 2026-10-01 and after 2026-10-31 which is impossible, returning an empty list

*Fixed*
-added `!` operator to `!isBefore` and `!isAfter` in Stream


_*Bug 3*_
Test `shouldThrowInvalidTransactionExceptionForInvalidDateInterval` failed:

-failed with `AssertionFailedError: Expected InvalidTransactionException to be thrown, but nothing was thrown.`
-placed breakpoint at start of  `filterByDateIntervalSorted()` and stepped through with debugger
-notived method started creating comparator and running `transactions.stream().filter()` without checking if endDate was before startDate or if arguments were null

*Fixed*
-added if statements catching null inputs at the beginning of `filterByDateIntervalSorted()` and at beginning of `filterByTypeSorted()`