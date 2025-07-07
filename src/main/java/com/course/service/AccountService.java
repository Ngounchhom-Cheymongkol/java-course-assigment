package com.course.service;

import com.course.repository.AccountRepository;
import com.course.repository.entity.Account;
import com.course.repository.entity.AccountTransaction;
import com.course.repository.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountService {
    public void createAccount(User user) {
        Logger.getLogger("org.hibernate.engine.internal.StatisticalLoggingSessionEventListener")
                .setLevel(Level.WARNING);
        Scanner myObj = new Scanner(System.in);
        AccountRepository accountRepository = new AccountRepository();

        System.out.println("👤➕ Account Creation");
        Account account = new Account();
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
        account.setAccountNo(String.format("%09d", accountRepository.getMaxId()));



        System.out.println("Currency");
        System.out.println("1. USD ");
        System.out.println("2. KHR ");
        System.out.print("Please choice : ");
        String currency = myObj.nextLine();
        if (currency.equals("1")) {
            account.setCurrency(Account.Currency.USD);
        }else {
            account.setCurrency(Account.Currency.KHR);
        }

        System.out.println("Account Type");
        System.out.println("1. CHECKING ");
        System.out.println("2. SAVINGS ");
        System.out.println("2. INVESTMENT ");
        System.out.print("Please choice : ");
        String accountType = myObj.nextLine();
        if (accountType.equals("1")) {
            account.setAccountType(Account.AccountType.SAVINGS);
        }else if (accountType.equals("2")){
            account.setAccountType(Account.AccountType.SAVINGS);
        }else {
            account.setAccountType(Account.AccountType.INVESTMENT);
        }

        account.setCreatedAt(LocalDate.now().atStartOfDay());
        account.setUpdatedAt(LocalDate.now().atStartOfDay());


        accountRepository.saveAccount(account);
        System.out.println(" ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅ ");
        System.out.println(" ✅ Account: " + account.getAccountNo() + " Create Successfully");
        System.out.println(" ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅ ");
        System.out.print(" Press any key to continue: ");
        String enter = myObj.nextLine();
    }
    public void allAccountByUser(User user) {
        Logger.getLogger("org.hibernate.engine.internal.StatisticalLoggingSessionEventListener")
                .setLevel(Level.WARNING);
        Scanner myObj = new Scanner(System.in);

        boolean life = true;
        while (life) {
            System.out.println("👤➕ Account Creation");

            AccountRepository accountRepository = new AccountRepository();
            List<Account> accounts = accountRepository.findAccountsByUserId(user.getId());

            System.out.println("👤➕ List User");
            System.out.println("➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖");
            System.out.printf("%-5s ❕ %-15s ❕ %-15s ❕ %-15s ❕ %-25s ❕%n", "No.", "Account No", "Currency", "Balance", "Created At");
            System.out.println("---------------------------------------------------------------------------------------------------");
            for (int i = 0; i < accounts.size(); i++) {
                Account item = accounts.get(i);
                System.out.printf("%-5s ❕ %-15s ❕ %-15s ❕ %-15s ❕ %-25s ❕%n", i + 1, item.getAccountNo(), item.getCurrency(), item.getBalance(), item.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            }
            System.out.println("➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖\n");
            System.out.println("1. Add Account ");
            System.out.println("2. Deposit ");
            System.out.println("3. Withdraw ");
            System.out.println("4. Transfer ");
            System.out.println("5. Transaction ");
            System.out.println("6. Exit ");
            System.out.print("Please enter your choice: ");
            String option = myObj.nextLine();
            if (option.equals("1")) {
                createAccount(user);
            }else if (option.equals("2")){
                System.out.print("Please enter your account Index: ");
                String index = myObj.nextLine();
                deposit(accounts.get(Integer.parseInt(index) - 1), user);
            }else if (option.equals("3")){
                System.out.print("Please enter your account Index: ");
                String index = myObj.nextLine();
                withDraw(accounts.get(Integer.parseInt(index) -1), user);
            }else if (option.equals("4")){
                System.out.print("Please enter your account Index: ");
                String index = myObj.nextLine();
                transfer( accounts.get(Integer.parseInt(index) -1), user);
            }else if (option.equals("5")){
                TransferService transferService = new TransferService();
                transferService.allTransactionByUser(user);
            }else {
                life = false;
            }
        }
    }

    public void deposit(Account account, User user) {
        Logger.getLogger("org.hibernate.engine.internal.StatisticalLoggingSessionEventListener")
                .setLevel(Level.WARNING);
        Scanner myObj = new Scanner(System.in);
        System.out.println("Deposit");
        System.out.println("➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖\n");
        System.out.print("Please enter deposit Amount:  ");
        String amount = myObj.nextLine();

        AccountRepository accountRepository = new AccountRepository();
        accountRepository.deposit(account.getId(), new BigDecimal(amount));

        TransferService transferService = new TransferService();
        transferService.save("110000000", account.getAccountNo(), user, new BigDecimal(amount) ,AccountTransaction.Type.DEPOSIT);
        System.out.println(" ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅ ");
        System.out.println(" ✅ You have been deposit "+ amount +" Successfully from account: " + account.getAccountNo());
        System.out.println(" ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅ ");
        System.out.print(" Press any key to continue: ");
        String enter = myObj.nextLine();
    }

    public void transfer(Account account, User user) {
        Logger.getLogger("org.hibernate.engine.internal.StatisticalLoggingSessionEventListener")
                .setLevel(Level.WARNING);
        Scanner myObj = new Scanner(System.in);
        System.out.println("Transfer");
        Optional<Account> destinationAcc = Optional.empty();
        boolean correct = true;
        Integer count = 1;
        while (correct) {
            System.out.println("➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖\n");
            System.out.print("Please enter destination Account:  ");
            String destination = myObj.nextLine();
            AccountRepository accountRepository = new AccountRepository();
            destinationAcc = accountRepository.findByAccountNo(destination);
            if (destinationAcc.isPresent()) {
                correct = false;
            }else {
                count++;
            }
            if (count > 3) {
                correct = false;
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println("\n\n ❌❌❌❌❌❌❌❌❌❌❌❌❌❌❌ \n");
                System.out.println(" You Input account incorrect please try again");
                System.out.println("  \n ❌❌❌❌❌❌❌❌❌❌❌❌❌❌❌ \n");
                System.out.print(" Press enter to continue: ");
                String enter = myObj.nextLine();
            }
        }
        if (count <= 3) {
            System.out.println("➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖\n");
            System.out.print("Please enter deposit Amount:  ");
            String amount = myObj.nextLine();
            AccountRepository accountRepository = new AccountRepository();
            accountRepository.deposit(destinationAcc.get().getId(), new BigDecimal(amount));
            accountRepository.withdraw(account.getId(), new BigDecimal(amount));

            TransferService transferService = new TransferService();
            transferService.save(account.getAccountNo(), destinationAcc.get().getAccountNo(), user, new BigDecimal(amount) ,AccountTransaction.Type.TRANSFER);
            System.out.println(" ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅ ");
            System.out.println(" ✅ Transfer Success From Acc:  " + account.getAccountNo() + "To : " +  destinationAcc.get().getAccountNo());
            System.out.println(" ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅ ");
            System.out.print(" Press any key to continue: ");
            String enter = myObj.nextLine();
        }
    }

    public void withDraw(Account account, User user) {
        Logger.getLogger("org.hibernate.engine.internal.StatisticalLoggingSessionEventListener")
                .setLevel(Level.WARNING);
        Scanner myObj = new Scanner(System.in);
        System.out.println("Deposit");
        System.out.println("➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖\n");
        System.out.print("Please enter deposit Amount:  ");
        String amount = myObj.nextLine();
        TransferService transferService = new TransferService();
        transferService.save(account.getAccountNo(), "220000000", account.getUser(), new BigDecimal(amount) ,AccountTransaction.Type.WITHDRAWAL);
        if (account.getBalance().compareTo(new BigDecimal(amount)) > 0 ) {
            AccountRepository accountRepository = new AccountRepository();
            accountRepository.withdraw(account.getId(), new BigDecimal(amount));

            System.out.println(" ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅ ");
            System.out.println(" ✅ You have been withdraw "+ amount +" Successfully from account: " + account.getAccountNo());
            System.out.println(" ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅ ");
            System.out.print(" Press any key to continue: ");
            String enter = myObj.nextLine();
        }else {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("\n\n ❌❌❌❌❌❌❌❌❌❌❌❌❌❌❌ \n");
            System.out.println(" Balance no enough");
            System.out.println("  \n ❌❌❌❌❌❌❌❌❌❌❌❌❌❌❌ \n");
            System.out.print(" Press enter to continue: ");
            String enter = myObj.nextLine();
        }

    }

}
