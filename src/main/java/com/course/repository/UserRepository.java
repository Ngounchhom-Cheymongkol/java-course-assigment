package com.course.repository;

import com.course.config.HibernateUtils;
import com.course.repository.entity.User;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


import java.util.List;
import java.util.Optional;

public class UserRepository {

    // CREATE operation
    public User saveUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            System.out.println("User saved successfully: " + user);
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error saving user: " + e.getMessage());
            throw new RuntimeException("Failed to save user", e);
        }
    }

    // READ operation - Find by ID
    public Optional<User> findUserById(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
//            Hibernate.initialize(user.getAccounts());
            return Optional.of(user);
        } catch (Exception e) {
            System.err.println("Error finding user by id: " + id + " - " + e.getMessage());
            throw new RuntimeException("Failed to find user", e);
        }
    }

    public Optional<User> findUserByIdIncludeAccounts(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            Hibernate.initialize(user.getAccounts());
            return Optional.of(user);
        } catch (Exception e) {
            System.err.println("Error finding user by id: " + id + " - " + e.getMessage());
            throw new RuntimeException("Failed to find user", e);
        }
    }

    // READ operation - Find by email
    public Optional<User> findUserByEmail(String email) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            System.err.println("Error finding user by email: " + email + " - " + e.getMessage());
            throw new RuntimeException("Failed to find user by email", e);
        }
    }

    // READ operation - Get all users with pagination
    public List<User> getAllUsers(int page, int size) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "FROM User u ORDER BY u.createdAt DESC", User.class);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
            throw new RuntimeException("Failed to get users", e);
        }
    }

    // READ operation - Get users with accounts (JOIN FETCH to avoid N+1)
    public List<User> getUsersWithAccounts() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.accounts ORDER BY u.name",
                    User.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error getting users with accounts: " + e.getMessage());
            throw new RuntimeException("Failed to get users with accounts", e);
        }
    }

    // UPDATE operation
    public User updateUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User updatedUser = session.merge(user);
            transaction.commit();
            System.out.println("User updated successfully: " + updatedUser);
            return updatedUser;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating user: " + e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        }
    }

    // DELETE operation
    public boolean deleteUser(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                System.out.println("User deleted successfully: " + id);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting user with id: " + id + " - " + e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    // Batch operation
    public void saveUsersInBatch(List<User> users) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            int batchSize = 25;
            for (int i = 0; i < users.size(); i++) {
                session.persist(users.get(i));

                if (i % batchSize == 0 && i > 0) {
                    session.flush();
                    session.clear();
                }
            }

            transaction.commit();
            System.out.println("Batch saved " + users.size() + " users successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error in batch save: " + e.getMessage());
            throw new RuntimeException("Failed to batch save users", e);
        }
    }

    // Count total users
    public Long getUserCount() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(u) FROM User u", Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("Error getting user count: " + e.getMessage());
            throw new RuntimeException("Failed to get user count", e);
        }
    }
}
