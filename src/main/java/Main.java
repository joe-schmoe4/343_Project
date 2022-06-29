import handlers.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        MenuHandler menu = MenuHandler.getInstance();
        FileHandler file = FileHandler.getInstance();


        // While Login Invalid...
        menu.promptLogin();

        // Load Saved Data
        file.loadData();

        // Prompt Main Menu
        menu.promptMainMenu();

        // Save Current Data
        file.saveData();

    }

}
