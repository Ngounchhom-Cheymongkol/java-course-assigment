package com.course;
import com.course.service.AccountService;
import com.course.service.UserService;

import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        Logger.getLogger("org.hibernate.engine.internal.StatisticalLoggingSessionEventListener")
                .setLevel(Level.WARNING);
        boolean auth = false;
        boolean life = true;
        while (life) {
            System.out.println("Option");
            System.out.println("1. 📃 List User");
            System.out.println("2. ➕ Create User");
            System.out.println("5. ❌ Close");

            Scanner myObj = new Scanner(System.in);
            System.out.print("Please enter your choice: ");
            String option = myObj.nextLine();
            if (option.equals("5")) {
                life = false;
            } else if (option.equals("1")) {
                UserService userService = new UserService();
                userService.allUser();
            } else if (option.equals("2")) {
                UserService userService = new UserService();
                userService.createUser();
            } else {
                // Clears the screen
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println("\n\n ❌❌❌❌❌❌❌❌❌❌❌❌❌❌❌ \n");
                System.out.println(" Incorrect Option Input");
                System.out.println("  \n ❌❌❌❌❌❌❌❌❌❌❌❌❌❌❌ \n");
                System.out.print(" Press enter to continue: ");
                String enter = myObj.nextLine();
            }
        }

    }
    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Could not clear console.");
        }
    }
}