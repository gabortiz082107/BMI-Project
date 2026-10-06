package com.bmitracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SINGLETON PATTERN: guarantees a single shared JDBC connection for the
 * whole application's lifetime, avoiding the overhead/bugs of opening
 * many redundant connections to the SQLite file.
 *
 * Uses SQLite via the sqlite-jdbc driver so the project runs with zero
 * external database server setup — perfect for a student capstone demo.
 * Swap the URL/driver here to point at MySQL, PostgreSQL, SQL Server,
 * etc. without changing any DAO code (see Crud interface).
 */
public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:bmidb.sqlite";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to initialize database connection.", e);
        }
    }

    /** Thread-safe lazy accessor for the single shared instance. */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    /** Creates the bmi_records table on first run if it doesn't already exist. */
    public void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS bmi_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "first_name TEXT NOT NULL," +
                "last_name TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "height_cm REAL NOT NULL," +
                "weight_kg REAL NOT NULL" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create bmi_records table.", e);
        }
    }
}
