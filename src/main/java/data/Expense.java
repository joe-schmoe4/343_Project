package data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import handlers.FileHandler;
import handlers.MenuHandler;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Expense {

    private static ArrayList<Expense> expenses;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private String category;
    private String payee;
    private double payment;

    /**
     * Default Constructor
     * Used by Jackson for JSON Mapper Reading.
     * Using this on its own accomplishes nothing.
     */
    public Expense() {}

    /**
     * Private Constructor
     * Creates a new Rent Object. Constructor called by addRent().
     */
    private Expense(LocalDate date, String category, String payee, double payment) {
        this.date = date;
        this.category = category;
        this.payee = payee;
        this.payment = payment;
    }

    /**
     * Creates a new Expense Object and adds it to the ArrayList.
     * This is the only way to add a new Expense. Constructor is private.
     * @param year Year of expense as int.
     * @param month Month of expense as int.
     * @param day Day of Month of expense as int.
     * @param category Category of Expense
     * @param payee Recipient of the payment.
     * @param payment Amount paid.
     * @return Expense object created, null if unsuccessful.
     */
    public static Expense addExpense(int year, int month, int day, String category, String payee, double payment) {
        if (expenses == null) {
            expenses = new ArrayList<>();
        }
        LocalDate d;
        try {
            d = LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            MenuHandler.systemMessage("Attempted to create Expense but Date is invalid.");
            return null;
        }
        Expense e = new Expense(d, category, payee, payment);
        expenses.add(e);
        return e;
    }

    /**
     * Loads Expenses from a File into Memory
     * Can only be called from FileHandler.class
     * @param data ArrayList loaded from a File.
     * @return True if successful, False otherwise.
     */
    public static boolean loadExpenses(ArrayList<Expense> data) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if ((stack.length >= 3) && !(stack[2].getClassName().equals(FileHandler.class.getName()))) {
            MenuHandler.systemMessage("An unknown class tried to edit the Rent list.");
            return false;
        }
        if (data == null || data.isEmpty()) {
            MenuHandler.systemMessage("No data found in expense.json... Ignoring...");
            return true;
        }
        Expense.expenses = data;
        return true;
    }

    /**
     * Retrieves the list of expenses saved to memory.
     * List is unmodifiable to protect the list of Expenses.
     * @return Unmodifiable List of Expenses
     */
    public static List<Expense> getExpenses() {
        if (expenses != null) {
            return Collections.unmodifiableList(expenses);
        }
        return null;
    }

    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public String getPayee() { return payee; }
    public double getPayment() { return payment; }

    /**
     * Returns if the expense is the same expense.
     * Performs the checks on all attributes.
     * @param o Object
     * @return True if (this == o), false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) { return true; }
        if (!(o instanceof Expense e)) { return false; }
        return this.getDate().equals(e.getDate()) &&
                this.getCategory().equalsIgnoreCase(e.getCategory()) &&
                this.getPayee().equalsIgnoreCase(e.getPayee()) &&
                this.getPayment() == e.getPayment();
    }

    @Override
    public String toString() {
        return this.payee + " (" + this.category + ") - " + this.payment + " (" + this.date.getMonth() + " " + this.date.getDayOfMonth() + ", " + this.date.getYear() + ")";
    }
}
