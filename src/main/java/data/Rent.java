package data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import handlers.FileHandler;
import handlers.MenuHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Rent {

    private static ArrayList<Rent> rent;
    private UUID tenantId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private double payment;

    /**
     * Default Constructor
     * Used by Jackson for JSON Mapper Reading.
     * Using this on its own accomplishes nothing.
     */
    public Rent() {}

    /**
     * Private Constructor
     * Creates a new Rent Object. Constructor called by addRent().
     */
    private Rent(UUID tenantId, LocalDate date, double payment) {
        this.tenantId = tenantId;
        this.date = date;
        this.payment = payment;
    }

    /**
     * Creates a new Tenant Object and adds it to the ArrayList.
     * This is the only way to add a new Tenant. Constructor is private.
     * @param tenant Tenant associated to the Rent
     * @param year Year of the Payment
     * @param month Month of the Payment
     * @param payment Payment Total
     * @return Rent object added.
     */
    public static Rent addRent(Tenant tenant, int year, int month, double payment) {
        if (rent == null) {
            rent = new ArrayList<>();
        }
        if (tenant == null || tenant.getId() == null) {
            MenuHandler.systemMessage("Attempted to add Rent but there is no valid Tenant to associate to.");
            return null;
        }
        if (month < 1 || month > 12) {
            MenuHandler.systemMessage("Attempted to add Rent but the month is invalid.");
            return null;
        }
        if (year < 0) {
            MenuHandler.systemMessage("Attempted to add Rent but the year is invalid.");
            return null;
        }
        Rent r = new Rent(tenant.getId(), LocalDate.of(year, month, 1), payment);
        rent.add(r);
        return r;
    }

    /**
     * Loads a Rent from a File into Memory
     * Can only be called from FileHandler.class
     * @param data ArrayList loaded from a File.
     * @return True if successful, False otherwise.
     */
    public static boolean loadRent(ArrayList<Rent> data) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if ((stack.length >= 3) && !(stack[2].getClassName().equals(FileHandler.class.getName()))) {
            MenuHandler.systemMessage("An unknown class tried to edit the Rent list.");
            return false;
        }
        if (data == null || data.isEmpty()) {
            MenuHandler.systemMessage("No data found in rent.json... Ignoring...");
            return true;
        }
        for (Rent r : data) {
            if (Tenant.getTenantByID(r.tenantId) == null) {
                MenuHandler.systemMessage("Invalid data found in rent.json... Ignoring...");
                return false;
            }
        }
        Rent.rent = data;
        return true;
    }

    /**
     * Retrieves the list of rent saved to memory.
     * List is unmodifiable to protect the list of Rent Payments.
     * @return Unmodifiable List of Rent Payments
     */
    public static List<Rent> getRent() {
        if (rent != null) {
            return Collections.unmodifiableList(rent);
        }
        return null;
    }

    @JsonIgnore
    public Tenant getTenant() { return Tenant.getTenantByID(tenantId); }
    public UUID getTenantId() { return tenantId; }
    public LocalDate getDate() { return date; }
    public double getPayment() { return payment; }

    public boolean checkDuplicate(Tenant t, LocalDate date, double payment) {
        if (this.payment != payment) { return false; }
        if (!this.date.equals(date)) { return false; }
        if (Tenant.getTenantByID(this.tenantId) == null) { return false; }
        if (!Tenant.getTenantByID(this.tenantId).equals(t)) { return false; }
        return true;
    }

    /**
     * Returns if the rent is the same rent.
     * Performs the checks on all attributes.
     * @param o Object
     * @return True if (this == o), false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) { return true; }
        if (!(o instanceof Rent r)) { return false; }
        return this.getTenant().equals(r.getTenant()) &&
                this.getDate().equals(r.getDate()) &&
                this.getPayment() == r.getPayment();
    }

    @Override
    public String toString() {
        return getTenant() + " - " + this.payment + " (" + this.date.getMonth() + " " + this.date.getYear() + ")";
    }
}
