package handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.Expense;
import data.Rent;
import data.Tenant;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controls all file saving and loading.
 * Utilizes Jackson Databind for JSON Processing.
 * This class saves and loads all files and passes it to MenuHandler / data package classes as needed.
 */
public class FileHandler {

    private static FileHandler instance;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final File dir = new File("." + System.getProperty("file.separator") + "save_data");
    private static final File fileTenant = new File(dir, "tenant.json");
    private static final File fileRent = new File(dir, "rent.json");
    private static final File fileExpense = new File(dir, "expense.json");
    private static final File fileLogin = new File(dir, "login.json");

    private FileHandler() {}

    /**
     * Returns instance of FileHandler.
     * Constructor cannot be accessed, use this for getting instance.
     * @return FileHandler Instance
     */
    public static FileHandler getInstance() {
        if (instance == null) {
            instance = new FileHandler();
        }
        return instance;
    }

    /**
     * Loads all data from saved files.
     * Rent must load AFTER Tenant, each Rent object is associated to a Tenant.
     * Takes advantage of Jackson Core for JSON Parsing.
     */
    public boolean loadData() {
        try {
            // Loading Tenant File
            if (fileTenant.exists() && fileTenant.length() > 0) {
                ArrayList<Tenant> data = mapper.readValue(fileTenant, new TypeReference<>() {});
                Tenant.loadTenants(data);
            } else {
                MenuHandler.systemMessage("Tenant save file does not exist or contains no data, ignoring...");
            }
            // Loading Rent File
            if (fileRent.exists() && fileRent.length() > 0) {
                ArrayList<Rent> data = mapper.readValue(fileRent, new TypeReference<>() {});
                Rent.loadRent(data);
            } else {
                MenuHandler.systemMessage("Rent save file does not exist or contains no data, ignoring...");
            }
            // Loading Expense File
            if (fileExpense.exists() && fileExpense.length() > 0) {
                ArrayList<Expense> data = mapper.readValue(fileExpense, new TypeReference<>() {});
                Expense.loadExpenses(data);
            } else {
                MenuHandler.systemMessage("Expense save file does not exist or contains no data, ignoring...");
            }
        } catch (IOException e) {
            e.printStackTrace();
            MenuHandler.systemMessage("An error has occurred, please look at the above Stacktrace for more info.");
            return false;
        }
        return true;
    }

    /**
     * Saves all data from memory.
     * Takes advantage of Jackson Core for JSON Building.
     * @return True if successful, False otherwise.
     */
    public boolean saveData() {

        // Check for Directory, create if missing...
        if (dir.mkdirs()) {
            MenuHandler.systemMessage("No directory found, created directory...");
        }

        // Try to create the files.
        try {
            if (fileTenant.createNewFile()) {
                MenuHandler.systemMessage("No tenant save file found, created file...");
            }
            if (fileRent.createNewFile()) {
                MenuHandler.systemMessage("No rent save file found, created file...");
            }
            if (fileExpense.createNewFile()) {
                MenuHandler.systemMessage("No expense save file found, created file...");
            }

            // Save Files
            mapper.writeValue(fileTenant, Tenant.getTenants());
            mapper.writeValue(fileRent, Rent.getRent());
            mapper.writeValue(fileExpense, Expense.getExpenses());

        } catch (IOException e) {
            e.printStackTrace();
            MenuHandler.systemMessage("Failed to load data files, please see System Administrator.");
            return false;
        }
        return true;
    }

    /**
     * Loads login details from Memory.
     * Login Details exist as a HashMap, associating a Hashed Username to a Hashed Password.
     * Defaults to "admin" and "password" by default.
     * @return HashMap containing all hashed users and passwords, null otherwise.
     */
    public static HashMap<String, String> getLoginDetails() {
        // Check for Directory, create if missing...
        if (dir.mkdirs()) {
            MenuHandler.systemMessage("No directory found, created directory...");
        }

        // Create file if missing.
        if (!fileLogin.exists() || fileLogin.length() == 0) {
            try {
                if (fileLogin.createNewFile()) {
                    MenuHandler.systemMessage("No login file found, created file...");
                }
                HashMap<String, String> defaultLogin = new HashMap<>();
                DigestUtils sha = new DigestUtils("SHA3-256");
                defaultLogin.put(sha.digestAsHex("admin"), sha.digestAsHex("password"));
                mapper.writeValue(fileLogin, defaultLogin);
            } catch (IOException e) {
                e.printStackTrace();
                MenuHandler.systemMessage("Failed to create login details file, please see System Administrator.");
                return null;
            }
        }

        // Return HashMap
        try {
            return mapper.readValue(fileLogin, new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            MenuHandler.systemMessage("Failed to load login details file, please see System Administrator.");
        }
        return null;
    }

}
