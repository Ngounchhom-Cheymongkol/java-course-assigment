package com.course.repository;

import com.course.config.HibernateUtils;
import com.course.repository.entity.Account;
import com.course.repository.entity.AccountTransaction;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class TransactionRepository {
    // CREATE operation
    public AccountTransaction saveTransaction(AccountTransaction accountTransaction) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(accountTransaction);
            transaction.commit();
            System.out.println("User saved successfully: " + accountTransaction);
            return accountTransaction;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error saving user: " + e.getMessage());
            throw new RuntimeException("Failed to save user", e);
        }
    }
    // READ operation - Find accounts by user
    public List<AccountTransaction> findTransactionsByUserId(Long userId) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<AccountTransaction> query = session.createQuery(
                    "FROM AccountTransaction a WHERE a.user.id = :userId ORDER BY a.createdAt",
                    AccountTransaction.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error finding accounts for user: " + userId + " - " + e.getMessage());
            throw new RuntimeException("Failed to find accounts for user", e);
        }
    }

}
