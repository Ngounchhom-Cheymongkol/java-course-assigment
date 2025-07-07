package com.course.repository;

import com.course.config.HibernateUtils;
import com.course.repository.entity.Account;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountRepository {

    // CREATE operation
    public Account saveAccount(Account account) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(account);
            transaction.commit();
            System.out.println("Account saved successfully: " + account);
            return account;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error saving account: " + e.getMessage());
            throw new RuntimeException("Failed to save account", e);
        }
    }

    // READ operation - Find by ID
    public Optional<Account> findAccountById(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Account account = session.get(Account.class, id);
            return Optional.ofNullable(account);
        } catch (Exception e) {
            System.err.println("Error finding account by id: " + id + " - " + e.getMessage());
            throw new RuntimeException("Failed to find account", e);
        }
    }

    // READ operation - Find accounts by user
    public List<Account> findAccountsByUserId(Long userId) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<Account> query = session.createQuery(
                    "FROM Account a WHERE a.user.id = :userId ORDER BY a.createdAt",
                    Account.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error finding accounts for user: " + userId + " - " + e.getMessage());
            throw new RuntimeException("Failed to find accounts for user", e);
        }
    }
    public Optional<Account> findByAccountNo(String accountNo) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Account result = session.createQuery(
                            "FROM Account a WHERE a.accountNo = :accountNo", Account.class)
                    .setParameter("accountNo", accountNo)
                    .uniqueResult();

            return Optional.ofNullable(result); // Wrap in Optional
        } catch (Exception e) {
            System.err.println("Error finding account by account number: " + e.getMessage());
            throw new RuntimeException("Failed to find account", e);
        }
    }

    // READ operation - Find accounts by type
    public List<Account> findAccountsByType(Account.AccountType accountType) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<Account> query = session.createQuery(
                    "FROM Account a WHERE a.accountType = :type ORDER BY a.balance DESC",
                    Account.class);
            query.setParameter("type", accountType);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error finding accounts by type: " + accountType + " - " + e.getMessage());
            throw new RuntimeException("Failed to find accounts by type", e);
        }
    }

    // UPDATE operation
    public Account updateAccount(Account account) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Account updatedAccount = session.merge(account);
            transaction.commit();
            System.out.println("Account updated successfully: " + updatedAccount);
            return updatedAccount;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating account: " + e.getMessage());
            throw new RuntimeException("Failed to update account", e);
        }
    }
    public Account deposit(Long id, BigDecimal amount) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Fetch the account by ID
            Account account = session.createQuery(
                            "FROM Account a WHERE a.id = :id", Account.class)
                    .setParameter("id", id)
                    .uniqueResult();

            if (account == null) {
                throw new RuntimeException("Account not found with ID: " + id);
            }

            // Update the balance
            account.setBalance(account.getBalance().add(amount));

            // Hibernate will track the change and update the DB on commit
            session.update(account); // Optional if the object is managed

            transaction.commit();
            return account;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating account: " + e.getMessage());
            throw new RuntimeException("Failed to update account", e);
        }
    }
    public Account withdraw(Long id, BigDecimal amount) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Fetch the account by ID
            Account account = session.createQuery(
                            "FROM Account a WHERE a.id = :id", Account.class)
                    .setParameter("id", id)
                    .uniqueResult();

            if (account == null) {
                throw new RuntimeException("Account not found with ID: " + id);
            }

            // Update the balance
            account.setBalance(account.getBalance().subtract(amount));

            // Hibernate will track the change and update the DB on commit
            session.update(account); // Optional if the object is managed

            transaction.commit();
            return account;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating account: " + e.getMessage());
            throw new RuntimeException("Failed to update account", e);
        }
    }


    // Money transfer operation with pessimistic locking
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Lock accounts for update to prevent concurrent modifications
            Account fromAccount = session.createQuery(
                            "FROM Account a WHERE a.id = :id", Account.class)
                    .setParameter("id", fromAccountId)
                    .setLockMode(LockMode.PESSIMISTIC_WRITE.toJpaLockMode())
                    .uniqueResult();

            Account toAccount = session.createQuery(
                            "FROM Account a WHERE a.id = :id", Account.class)
                    .setParameter("id", toAccountId)
                    .setLockMode(LockMode.PESSIMISTIC_WRITE.toJpaLockMode())
                    .uniqueResult();

            if (fromAccount == null || toAccount == null) {
                throw new IllegalArgumentException("Account not found");
            }

            // Perform the transfer using entity methods
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);

            session.merge(fromAccount);
            session.merge(toAccount);

            transaction.commit();
            System.out.println("Transfer completed: " + amount + " from account " +
                    fromAccountId + " to account " + toAccountId);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error in money transfer from " + fromAccountId +
                    " to " + toAccountId + ": " + e.getMessage());
            throw new RuntimeException("Failed to transfer money", e);
        }
    }

    // Get account summary with aggregations
    public List<Object[]> getAccountSummaryByUser() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<Object[]> query = session.createQuery(
                    "SELECT u.name, COUNT(a.id), COALESCE(SUM(a.balance), 0), AVG(a.balance) " +
                            "FROM User u LEFT JOIN u.accounts a " +
                            "GROUP BY u.id, u.name " +
                            "ORDER BY COALESCE(SUM(a.balance), 0) DESC",
                    Object[].class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error getting account summary: " + e.getMessage());
            throw new RuntimeException("Failed to get account summary", e);
        }
    }

    // Get accounts with balance above threshold
    public List<Account> getAccountsAboveBalance(BigDecimal threshold) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<Account> query = session.createQuery(
                    "FROM Account a JOIN FETCH a.user WHERE a.balance > :threshold ORDER BY a.balance DESC",
                    Account.class);
            query.setParameter("threshold", threshold);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error getting accounts above balance: " + e.getMessage());
            throw new RuntimeException("Failed to get accounts above balance", e);
        }
    }

    // DELETE operation
    public boolean deleteAccount(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Account account = session.get(Account.class, id);
            if (account != null) {
                session.remove(account);
                transaction.commit();
                System.out.println("Account deleted successfully: " + id);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting account with id: " + id + " - " + e.getMessage());
            throw new RuntimeException("Failed to delete account", e);
        }
    }

    // Get total balance across all accounts
    public BigDecimal getTotalBalance() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<BigDecimal> query = session.createQuery(
                    "SELECT COALESCE(SUM(a.balance), 0) FROM Account a", BigDecimal.class);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("Error getting total balance: " + e.getMessage());
            throw new RuntimeException("Failed to get total balance", e);
        }
    }
    // Get total balance across all accounts
    public Long getMaxId() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "select max(a.id) FROM Account a", Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("Error getting total balance: " + e.getMessage());
            throw new RuntimeException("Failed to get total balance", e);
        }
    }
}
