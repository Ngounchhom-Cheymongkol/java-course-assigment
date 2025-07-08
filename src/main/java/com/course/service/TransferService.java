package com.course.service;

import com.course.repository.AccountRepository;
import com.course.repository.TransactionRepository;
import com.course.repository.entity.Account;
import com.course.repository.entity.AccountTransaction;
import com.course.repository.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransferService {
    public void save(String source, String destination, User user, BigDecimal amount, AccountTransaction.Type type) {
        TransactionRepository transactionRepository = new TransactionRepository();
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setSourceAcc(source);
        accountTransaction.setDestinationAcc(destination);
        accountTransaction.setCreatedAt(LocalDate.now().atStartOfDay());
        accountTransaction.setUpdatedAt(LocalDate.now().atStartOfDay());
        accountTransaction.setBalance(amount);
        accountTransaction.setUser(user);
        accountTransaction.setTranType(type);
        accountTransaction.setRef(UUID.randomUUID().toString());
        transactionRepository.saveTransaction(accountTransaction);
    }

    public void allTransactionByUser(User user) {
        Logger.getLogger("org.hibernate.engine.internal.StatisticalLoggingSessionEventListener")
                .setLevel(Level.WARNING);
        Scanner myObj = new Scanner(System.in);

        System.out.println("👤➕ User Transaction");

        TransactionRepository transactionRepository = new TransactionRepository();
        List<AccountTransaction> accountTransaction = transactionRepository.findTransactionsByUserId(user.getId());

        System.out.println("👤➕ List Transaction");
        System.out.println("➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖");
        System.out.printf("%-5s ❕ %-15s ❕ %-15s ❕ %-15s ❕ %-15s ❕ %-40s ❕ %-25s ❕%n", "No.", "Source", "Destination", "Amount", "Type", "Ref", "Created At");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < accountTransaction.size(); i++) {
            AccountTransaction item = accountTransaction.get(i);
            String destination = Objects.equals(item.getDestinationAcc(), "220000000") ? "Withdraw" : item.getDestinationAcc();
            String source = Objects.equals(item.getSourceAcc(), "110000000") ? "Deposit" : item.getSourceAcc();
            System.out.printf("%-5s ❕ %-15s ❕ %-15s ❕ %-15s ❕ %-15s ❕ %-40s ❕ %-25s ❕%n", i + 1, source, destination, item.getBalance(), item.getTranType(), item.getRef(), item.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        }
        System.out.println("➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖\n");

        System.out.print("Press enter to continue .... ");
        String option = myObj.nextLine();

    }
}
