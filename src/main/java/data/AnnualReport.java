package data;

import java.util.ArrayList;

public class AnnualReport {

    private static final String REPORT_OUT =
            """
                     Annual Report (%d)
                     Total Earnings:  %.2f
                     Total Expenses:  %.2f
                     ----------------------
                    Net Income:      %.2f 
                    """;

    /**
     * Calculates rent profits and expenses. Subtracts profits from expenses to get balances
     * @return String of report
     */
    public static String generateReport(int year){
        double earnings = 0.0;
        double expenses = 0.0;

        if (Rent.getRent() != null) {
            for (Rent r : Rent.getRent()) {
                if (r.getDate().getYear() == year) {
                    earnings += r.getPayment();
                }
            }
        }

        if (Expense.getExpenses() != null) {
            for (Expense e : Expense.getExpenses()) {
                if (e.getDate().getYear() == year) {
                    expenses += e.getPayment();
                }
            }
        }

        expenses = expenses*-1.0;

        return String.format(REPORT_OUT, year, earnings, expenses, earnings+expenses);

    }
}