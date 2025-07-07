package com.course.config;

import com.course.repository.entity.Account;
import com.course.repository.entity.AccountTransaction;
import com.course.repository.entity.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;


import java.util.Properties;

public class HibernateUtils {
    private static SessionFactory sessionFactory;
    private static HikariDataSource dataSource;

    static {
        try {
            // Create HikariCP DataSource
            dataSource = createHikariDataSource();

            // Create Hibernate Configuration
            Configuration configuration = new Configuration();

            // Set Hibernate properties programmatically
            Properties hibernateProperties = getHibernateProperties();
            configuration.setProperties(hibernateProperties);

            // Add annotated classes
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Account.class);
            configuration.addAnnotatedClass(AccountTransaction.class);

            // Build ServiceRegistry
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .applySetting("hibernate.connection.datasource", dataSource)
                    .build();

            // Build SessionFactory
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);

            System.out.println("Hibernate SessionFactory created successfully with HikariCP");

        } catch (Exception e) {
            System.err.println("Failed to create SessionFactory: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    private static HikariDataSource createHikariDataSource() {
        HikariConfig config = new HikariConfig();

        // Database connection settings
        config.setJdbcUrl("jdbc:postgresql://localhost:5435/java_course_db");
        config.setUsername("postgres");
        config.setPassword("123456");
        config.setDriverClassName("org.postgresql.Driver");

        // Pool settings
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);

        // PostgreSQL optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("ApplicationName", "HibernateHikariCPDemo");

        // Pool name for monitoring
        config.setPoolName("HikariCP-PostgreSQL");

        HikariDataSource ds = new HikariDataSource(config);
        System.out.println("HikariCP DataSource created successfully");
        return ds;
    }

    private static Properties getHibernateProperties() {
        Properties properties = new Properties();

        // Connection provider
        properties.setProperty("hibernate.connection.provider_class",
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        // Database dialect
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        // Schema management
        properties.setProperty("hibernate.hbm2ddl.auto", "update");

        // SQL logging and formatting
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.use_sql_comments", "true");

        // Performance settings
        properties.setProperty("hibernate.jdbc.batch_size", "25");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");

        // Cache settings
        properties.setProperty("hibernate.cache.use_second_level_cache", "false");
        properties.setProperty("hibernate.cache.use_query_cache", "false");

        // Statistics
        properties.setProperty("hibernate.generate_statistics", "true");

        // Connection handling
        properties.setProperty("hibernate.connection.autocommit", "false");
        properties.setProperty("hibernate.current_session_context_class", "thread");

        return properties;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static void printConnectionPoolStats() {
        if (dataSource != null) {
            System.out.println("=== HikariCP Pool Statistics ===");
            System.out.println("Active connections: " + dataSource.getHikariPoolMXBean().getActiveConnections());
            System.out.println("Idle connections: " + dataSource.getHikariPoolMXBean().getIdleConnections());
            System.out.println("Total connections: " + dataSource.getHikariPoolMXBean().getTotalConnections());
            System.out.println("Threads awaiting connection: " + dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (dataSource != null) {
            dataSource.close();
        }
        System.out.println("Hibernate SessionFactory and HikariCP DataSource closed");
    }
}
