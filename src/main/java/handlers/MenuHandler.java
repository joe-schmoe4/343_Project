package handlers;
import data.AnnualReport;
import data.Expense;
import data.Rent;
import data.Tenant;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDate;
import java.util.*;

/**
 * Controls all menus and is the main execution control of the program.
 * All Console Inputs and Outputs go through here.
 * This menu will call data package classes for object creation.
 */
public class MenuHandler {

    private static MenuHandler instance;
    private static final String MAIN_MENU_PROMPT =
            """
                    Main Menu 
                    Please select an option:
                    i) Input Data
                    d) Display Reports
                    q) Quit
                    """;
    private static final String INPUT_PROMPT =
            """
                     Input Menu 
                     Please select an option:
                     t) Record Tenant Information
                     r) Record Rent Payment
                     e) Record Expense
                     q) Return to Main Menu
                     """;
    private static final String DISPLAY_REPORTS_PROMPT =
            """
                     Display Reports Menu 
                     Please select an option:
                     t) Tenant Records
                     e) Expense Records
                     r) Rent Records
                     a) Annual Report
                     q) Return to Main Menu
                    """;
    private static final String INPUT_TENANT =
            """
                     Inputting Tenant 
                     NOTE: This operation assumes the most recent
                           tenant input will be the current resident.
                     Input "q" to return to the previous menu.
                     Press [ENTER] to continue
                   """;
    private static final String INPUT_EXPENSE =
            """
                     Expense Notice 
                     The expense requires you to have the month, day, year of payment,
                     the payment amount, payee name, and category of expense.
                     Most information is for reporting.
                    
                     Input "q" to return to the previous menu.
                     Press [ENTER] to continue.
                    """;
    private static final String INPUT_RENT =
            """
                     Rent Notice 
                     NOTE: Rent will require you to have the Tenant's name
                           and apartment number.
                    
                     The Rent input assumes the most recent entry to Tenants
                     that matches the name and apartment number is the current one.
                    
                     Input "q" to return to the previous menu.
                     Press [ENTER] to continue.
                    """;
    private static final String RECENT_TENANT =
            """
                     Tenant Warning 
                     There is currently a tenant assigned to this apartment number:
                       %s
                    
                     By inputting this new tenant, all new rent payments for this apartment
                     number will be associated to this tenant.
                    
                     Input "q" to return to the previous menu.
                     Press [ENTER] to continue and override this tenant.
                    """;
    private static final String NEW_RENT =
            """
                     Rent Confirmation 
                     There is currently a tenant assigned to this apartment number:
                       %s
                     Input "q" to return to the previous menu.
                     Press [ENTER] to continue and create the new rent entry.
                    """;

    private MenuHandler() {}

    /**
     * Returns instance of MenuHandler.
     * Constructor cannot be accessed, use this for getting instance.
     * @return MenuHandler Instance
     */
    public static MenuHandler getInstance() {
        if (instance == null) {
            instance = new MenuHandler();
        }
        return instance;
    }

    /**
     * Prompts the user for login details.
     * @return True on successful login, False otherwise.
     */
    public void promptLogin() {
        DigestUtils sha = new DigestUtils("SHA3-256");
        Scanner scan = new Scanner(System.in);
        String username, password;

        // Fetch Login Details
        HashMap<String, String> loginDetails = FileHandler.getLoginDetails();
        while (true) {
            if (loginDetails == null) {
                MenuHandler.systemMessage("An error has occurred with the Login System. Please contact a System Administrator.");
                MenuHandler.systemMessage("Press [ENTER] to continue. The program will be stuck in a login loop if not resolved.");
                scan.nextLine();
                continue;
            }

            // Prompt Login Details
            System.out.println("Please login with your username and password.");
            System.out.print("Username: ");
            username = sha.digestAsHex(scan.nextLine());
            System.out.print("Password: ");
            password = sha.digestAsHex(scan.nextLine());

            // Check Login Details
            if (loginDetails.containsKey(username) && loginDetails.get(username).equals(password)) { break; }
            MenuHandler.systemMessage("Incorrect username or password. Please try again.");
        }
    }

    /**
     * Prompt Main Menu and take user input.
     */
    public void promptMainMenu() {
        Scanner scan = new Scanner(System.in);
        String input = "";

        while (!input.equalsIgnoreCase("q")) {
            System.out.println(MAIN_MENU_PROMPT);
            System.out.print("Your Choice: ");
            input = scan.nextLine().toLowerCase();

            switch(input) {
                case "i" -> promptInputMenu();
                case "d" -> promptReportMenu();
                case "q" -> {}
                default -> MenuHandler.systemMessage("Your input is invalid, please try again...");
            }
        }

        MenuHandler.systemMessage("Exiting program...");
    }

    /**
     * Prompt Input Menus and take user input.
     */
    public void promptInputMenu() {
        Scanner scan = new Scanner(System.in);
        String input = "";
        boolean loop = true;

        while (loop) {
            System.out.println(INPUT_PROMPT);
            System.out.print("Your Choice: ");
            input = scan.nextLine().toLowerCase();

            switch(input) {
                case "t" -> promptInputTenant();
                case "r" -> promptInputRent();
                case "e" -> promptInputExpense();
                case "q" -> loop = false;
                default -> MenuHandler.systemMessage("Your input is invalid, please try again...");
            }

            FileHandler.getInstance().saveData();
        }

        MenuHandler.systemMessage("Returning to main menu...");
    }

    public void promptReportMenu() {
        Scanner scan = new Scanner(System.in);
        String input = "";
        boolean loop = true;

        while (loop) {
            System.out.println(DISPLAY_REPORTS_PROMPT);
            System.out.print("Your Choice: ");
            input = scan.nextLine().toLowerCase();

            switch(input) {
                case "t" -> displayTenants();
                case "e" -> displayExpenses();
                case "r" -> displayRent();
                case "a" -> displayAnnualReport();
                case "q" -> loop = false;
                default -> MenuHandler.systemMessage("Your input is invalid, please try again...");
            }
        }

        MenuHandler.systemMessage("Returning to main menu...");
    }

    public void displayTenants() {
        List<Tenant> tenants = Tenant.getTenants();

        // If there are no tenants
        if (tenants == null || tenants.isEmpty()) {
            MenuHandler.systemMessage("There are no Tenants to display...");
            return;
        }

        // Build tenants string to print out
        StringBuilder display = new StringBuilder("Display Tenants \n");
        for (int i = tenants.size()-1; i >= 0; i--) {
            display.append(String.format("%s\n", tenants.get(i)));
        }
        display.append("");

        System.out.println(display);
    }

    public void displayExpenses() {
        List<Expense> expenses = Expense.getExpenses();

        // If there are no expenses
        if (expenses == null || expenses.isEmpty()) {
            MenuHandler.systemMessage("There are no Expenses to display...");
            return;
        }

        // Build expenses string to print out
        StringBuilder display = new StringBuilder("Display Expenses \n");
        for (int i = expenses.size()-1; i >= 0; i--) {
            display.append(String.format("%s\n", expenses.get(i)));
        }
        display.append("");

        System.out.println(display);
    }

    public void displayRent() {
        List<Rent> rent = Rent.getRent();

        // If there are no rent records
        if (rent == null || rent.isEmpty()) {
            MenuHandler.systemMessage("There are no Rent Payments to display...");
            return;
        }

        // Build rent records string to print out
        StringBuilder display = new StringBuilder("Display Rent \n");
        for (int i = rent.size()-1; i >= 0; i--) {
            display.append(String.format("%s\n", rent.get(i)));
        }
        display.append("");

        System.out.println(display);
    }

    public void displayAnnualReport() {
        System.out.print("Enter the year for the report: ");
        System.out.println(AnnualReport.generateReport(getPositiveInt()));
    }

    /**
     * Used to deliver system messages directly to the user.
     */
    public static void systemMessage(String message) {
        System.out.println("[SYSTEM] " + message);
    }

    /**
     * Prompts tenant input
     */
    public void promptInputTenant() {
        Scanner scan = new Scanner(System.in);
        String name;
        int apt;

        // Display Tenant Notice and cancel
        System.out.println(INPUT_TENANT);
        if (scan.nextLine().equalsIgnoreCase("q")) { return; }

        // Prompt for inputs
        System.out.print("Enter tenant's name: ");
        name = scan.nextLine();
        System.out.print("Enter tenant's apartment number: ");
        apt = getPositiveInt();

        // Search for possible conflict
        List<Tenant> tenantList = Tenant.getTenants();
        if (tenantList != null && !tenantList.isEmpty()) {
            Tenant t = null;
            // Search for the most recent tenant with a conflict
            for (int i = tenantList.size() - 1; i >= 0; i--) {
                if (apt == tenantList.get(i).getAptNum()) {
                    t = tenantList.get(i);
                    break;
                }
            }
            // If there is a conflict, display warning and take input for quit or continue
            if (t != null) {
                System.out.printf(RECENT_TENANT + "\n", t);
                if (scan.nextLine().equalsIgnoreCase("q")) { return; }
            }
        }
        MenuHandler.systemMessage("Added Tenant: " + Tenant.addTenant(name, apt));
    }

    /**
     * Prompts rent menu
     */
    public void promptInputRent() {
        List<Tenant> tenantList = Tenant.getTenants();
        Scanner scan = new Scanner(System.in);
        int apt, year, month;
        double payment;

        // Check if any tenants exist.
        if (tenantList == null || tenantList.isEmpty()) {
            MenuHandler.systemMessage("There are no tenants to add rent to.");
            return;
        }

        // Display Rent notice and cancel
        System.out.println(INPUT_RENT);
        if (scan.nextLine().equalsIgnoreCase("q")) { return; }

        // Prompt for input
        System.out.print("Enter tenant's apartment number: ");
        apt = getPositiveInt();
        System.out.print("Enter year: ");
        year = getPositiveInt();
        System.out.print("Enter month: ");
        month = getIntRange(1, 12);
        System.out.print("Enter payment: ");
        payment = getPositiveDouble();

        // Search for the most recent tenant with matching apartment number
        Tenant t = null;
        for (int i = tenantList.size() - 1; i >= 0; i--) {
            if (apt == tenantList.get(i).getAptNum()) {
                t = tenantList.get(i);
                break;
            }
        }

        // Confirm rent association
        if (t != null) {
            // Duplicate check
            if (Rent.getRent() != null) {
                for (Rent r : Rent.getRent()) {
                    if (r.checkDuplicate(t, LocalDate.of(year, month, 1), payment)) {
                        MenuHandler.systemMessage("This is a duplicate entry, ignoring...");
                    }
                }
            }

            System.out.printf(NEW_RENT + "\n", t);
            if (scan.nextLine().equalsIgnoreCase("q")) { return; }
            MenuHandler.systemMessage("Added Rent: " + Rent.addRent(t, year, month, payment));
        }
        MenuHandler.systemMessage("There is no tenant to associate to.");
    }

    /**
     * Prompts expense menu
     * TODO: Look over and resolve any issues.
     */
    public void promptInputExpense() {
        Scanner scan = new Scanner(System.in);

        // Display Rent notice and cancel
        System.out.println(INPUT_EXPENSE);
        if (scan.nextLine().equalsIgnoreCase("q")) { return; }

        System.out.print("Enter month (1-12): ");
        int month = getIntRange(1,12);

        System.out.print("Enter day (1-31): ");
        int day = getIntRange(1,31);

        System.out.print("Enter year: ");
        int year = getPositiveInt();

        System.out.print("Enter expense category (Repairing, Utilities): ");
        String category = scan.nextLine();

        System.out.print("Enter payee (Bob's Hardware, Big Electric Co): ");
        String payee = scan.nextLine();

        System.out.print("Enter amount: ");
        double amount = getPositiveDouble();

        Expense.addExpense(year, month, day, category, payee, amount);
    }

    /**
     * Takes in an input, validates input for correct range and returns it
     * @param low int
     * @param high int
     * @return a valid int that fits in the range of low and high
     */
    public int getIntRange( int low, int high ) {
        Scanner in = new Scanner( System.in );
        int input = 0;
        boolean valid = false;
        while( !valid ) {
            if( in.hasNextInt() ) {
                input = in.nextInt();
                if( input <= high && input >= low ) {
                    valid = true;
                } else {
                    System.out.println( "Invalid number! Reenter your input: " );

                }
            } else {
                in.next();
                System.out.println( "Invalid Input. Reenter your input: " );
            }
        }
        return input;
    }

    /**
     * Takes in an input, validates input for positive int and returns it
     * @return positive int
     */
    public int getPositiveInt( ) {
        Scanner in = new Scanner( System.in );
        int input = 0;
        boolean valid = false;
        while( !valid ) {
            if( in.hasNextInt() ) {
                input = in.nextInt();
                if( input >= 0 ) {
                    valid = true;
                } else {
                    System.out.println( "Reenter a positive integer: " );
                }
            } else {
                in.next();
                System.out.println( "Invalid Input. Reenter a positive integer: " );
            }
        }
        return input;
    }

    /**
     * Takes in an input, validates input for positive double and returns it
     * @return positive double
     */
    public double getPositiveDouble( ) {
        Scanner in = new Scanner( System.in );
        double input = 0.0;
        boolean valid = false;
        while( !valid ) {
            if( in.hasNextDouble() ) {
                input = in.nextDouble();
                if( input >= 0 ) {
                    valid = true;
                } else {
                    System.out.println( "Reenter a positive number: " );
                }
            } else {
                in.next();
                System.out.println( "Invalid Input. Reenter a positive number: " );
            }
        }
        return input;
    }

}
