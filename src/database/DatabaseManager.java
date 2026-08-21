package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:database.db";
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private Connection connection;

    public DatabaseManager() {
        if (initConnection()) {
            setupDatabase();
        } else {
            logger.severe("Database setup aborted due to failed connection initialization.");
        }
    }

    public synchronized boolean initConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);

                // Enforce SQLite Foreign Key constraints per connection
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }

                logger.info("Database connection successfully established to " + DB_URL);
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection failed. Ensure sqlite-jdbc driver is in classpath: " + e.getMessage(), e);
            return false;
        }
    }

    private void createUserAccountTable() {
        String query = """
            CREATE TABLE IF NOT EXISTS userAccount (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                government_id TEXT NOT NULL UNIQUE,
                role TEXT NOT NULL,
                password TEXT NOT NULL,
                raw_cv_text TEXT,
                preferred_category TEXT,
                work_setup TEXT,
                employment_type TEXT,
                preferred_location TEXT
            );
            """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            logger.info("userAccount table initialized.");
        } catch (SQLException e) {
            logger.severe("Failed to initialize userAccount table: " + e.getMessage());
        }
    }

    private void createJobsTable() {
        String query = """
            CREATE TABLE IF NOT EXISTS jobs (
                job_id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                employer_name TEXT NOT NULL,
                description TEXT NOT NULL,
                location TEXT NOT NULL,
                contact_number TEXT NOT NULL,
                pay_info TEXT DEFAULT 'To be Discussed',
                schedule_info TEXT DEFAULT 'Flexible'
            );
            """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            logger.info("jobs table initialized.");
        } catch (SQLException e) {
            logger.severe("Failed to initialize jobs table: " + e.getMessage());
        }
    }

    private void createSkillsTable() {
        String query = """
            CREATE TABLE IF NOT EXISTS masterSkills (
                skill_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );
            """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            logger.info("masterSkills table initialized.");
        } catch (SQLException e) {
            logger.severe("Failed to initialize masterSkills table: " + e.getMessage());
        }
    }

    private void createAccommodationsTable() {
        String query = """
            CREATE TABLE IF NOT EXISTS accommodations (
                accommodation_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );
            """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            logger.info("accommodations table initialized.");
        } catch (SQLException e) {
            logger.severe("Failed to initialize accommodations table: " + e.getMessage());
        }
    }

    private void createUserSkillsTable() {
        String query = """
            CREATE TABLE IF NOT EXISTS userSkills (
                user_id INTEGER NOT NULL,
                skill_id INTEGER NOT NULL,
                PRIMARY KEY (user_id, skill_id),
                FOREIGN KEY (user_id) REFERENCES userAccount(user_id) ON DELETE CASCADE,
                FOREIGN KEY (skill_id) REFERENCES masterSkills(skill_id) ON DELETE CASCADE
            );
            """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            logger.info("userSkills table initialized.");
        } catch (SQLException e) {
            logger.severe("Failed to initialize userSkills table: " + e.getMessage());
        }
    }

    private void createJobSkillsTable() {
        String query = """
            CREATE TABLE IF NOT EXISTS job_skills (
                job_id INTEGER NOT NULL,
                skill_id INTEGER NOT NULL,
                PRIMARY KEY (job_id, skill_id),
                FOREIGN KEY (job_id) REFERENCES jobs(job_id) ON DELETE CASCADE,
                FOREIGN KEY (skill_id) REFERENCES masterSkills(skill_id) ON DELETE CASCADE
            );
            """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            logger.info("job_skills table initialized.");
        } catch (SQLException e) {
            logger.severe("Failed to initialize job_skills table: " + e.getMessage());
        }
    }

    private void createUserAccommodationsTable() {
        String query = """
            CREATE TABLE IF NOT EXISTS user_accommodations (
                user_id INTEGER NOT NULL,
                accommodation_id INTEGER NOT NULL,
                PRIMARY KEY (user_id, accommodation_id),
                FOREIGN KEY (user_id) REFERENCES userAccount(user_id) ON DELETE CASCADE,
                FOREIGN KEY (accommodation_id) REFERENCES accommodations(accommodation_id) ON DELETE CASCADE
            );
            """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            logger.info("user_accommodations table initialized.");
        } catch (SQLException e) {
            logger.severe("Failed to initialize user_accommodations table: " + e.getMessage());
        }
    }

    private void createJobAccommodationsTable() {
        String query = """
            CREATE TABLE IF NOT EXISTS job_accommodations (
                job_id INTEGER NOT NULL,
                accommodation_id INTEGER NOT NULL,
                PRIMARY KEY (job_id, accommodation_id),
                FOREIGN KEY (job_id) REFERENCES jobs(job_id) ON DELETE CASCADE,
                FOREIGN KEY (accommodation_id) REFERENCES accommodations(accommodation_id) ON DELETE CASCADE
            );
            """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
            logger.info("job_accommodations table initialized.");
        } catch (SQLException e) {
            logger.severe("Failed to initialize job_accommodations table: " + e.getMessage());
        }
    }

    public void setupDatabase() {
        createUserAccountTable();
        migrateUserAccountTable();
        createJobsTable();
        createSkillsTable();
        createAccommodationsTable();
        createUserSkillsTable();
        createJobSkillsTable();
        createUserAccommodationsTable();
        createJobAccommodationsTable();
    }

    private void migrateUserAccountTable() {
        ensureColumnExists("userAccount", "raw_cv_text", "TEXT");
        ensureColumnExists("userAccount", "preferred_category", "TEXT");
        ensureColumnExists("userAccount", "work_setup", "TEXT");
        ensureColumnExists("userAccount", "employment_type", "TEXT");
        ensureColumnExists("userAccount", "preferred_location", "TEXT");
    }

    private void ensureColumnExists(String tableName, String columnName, String columnType) {
        String tableInfoQuery = "PRAGMA table_info(" + tableName + ")";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(tableInfoQuery)) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return;
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to inspect table schema: " + e.getMessage());
            return;
        }

        String alterQuery = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType;
        try (Statement statement = connection.createStatement()) {
            statement.execute(alterQuery);
            logger.info("Added missing column " + columnName + " to " + tableName + ".");
        } catch (SQLException e) {
            logger.severe("Failed to add column " + columnName + " to " + tableName + ": " + e.getMessage());
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
            logger.info("Database connection closed.");
        }
    }

    public synchronized Connection getConnection() {
        initConnection();
        if (connection == null) {
            throw new IllegalStateException("Database connection is unavailable.");
        }
        return connection;
    }
}