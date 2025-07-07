package com.course.service;

import com.course.repository.UserRepository;
import com.course.repository.entity.User;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {

    public void createUser() {
        Logger.getLogger("org.hibernate.engine.internal.StatisticalLoggingSessionEventListener")
                .setLevel(Level.WARNING);
        Scanner myObj = new Scanner(System.in);
        System.out.println("👤➕ User Creation");
        User user = new User();
        System.out.print("Enter name: ");
        String name = myObj.nextLine();
        user.setName(name);

        System.out.print("Enter email: ");
        String email = myObj.nextLine();
        user.setEmail(email);

        user.setCreatedAt(LocalDate.now().atStartOfDay());
        user.setUpdatedAt(LocalDate.now().atStartOfDay());

        UserRepository userRepository = new UserRepository();
        userRepository.saveUser(user);
        System.out.println(" ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅ ");
        System.out.println(" ✅ User: " + user.getName() + "Create Successfully.");
        System.out.println(" ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅ ");
        System.out.print(" Press any key to continue: ");
        String enter = myObj.nextLine();
    }
    public void allUser() {
        Logger.getLogger("org.hibernate.engine.internal.StatisticalLoggingSessionEventListener")
                .setLevel(Level.WARNING);
        Scanner myObj = new Scanner(System.in);
        UserRepository userRepository = new UserRepository();
        List<User> users = userRepository.getAllUsers(0, 10);
        System.out.println("👤➕ List User");
        System.out.println("➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖");
        System.out.printf("%-5s ❕ %-15s ❕ %-35s ❕ %-25s ❕ %-25s ❕%n", "ID", "Name", "Email", "Created At", "Updated At");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < users.size(); i++) {
            User item = users.get(i);
            System.out.printf("%-5s ❕ %-15s ❕ %-35s ❕ %-25s ❕ %-25s ❕%n", i + 1, item.getName(), item.getEmail(), item.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), item.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        }
        System.out.println("➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖ \n"    );
        System.out.println("1. User Detail ");
        System.out.println("2. Exit ");
        System.out.println("Please enter your choice: ");
        String option = myObj.nextLine();
        if (option.contains("1")) {
            System.out.print("1. Enter User No (Ex: 1, 2, ...) : ");
            String userIndex = myObj.nextLine();
            User user = users.get(Integer.parseInt(userIndex) - 1);
            System.out.println("Name :  " + user.getName());
            System.out.println("Email :  " + user.getEmail());
            AccountService accountService = new AccountService();
            accountService.allAccountByUser(users.get(Integer.parseInt(userIndex)));
        }
    }
}
