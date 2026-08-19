package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DatabaseManager {

    private Connection connection;
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());

    public DatabaseManager() {
        initConnection();
        setupDatabase();
    }

    public void initConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:sqlite:database.db");

                // Enforce SQLite Foreign Key constraints
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }

                logger.info("Database connection initialized with foreign keys enabled");
            }
        } catch (SQLException e) {
            logger.severe("Database connection error: " + e.getMessage());
        }
    }

    private void createUserAccountTable() {
        String query = """
            CREATE TABLE IF NOT EXISTS userAccount (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                username TEXT NOT NULL UNIQUE,
                contact_number TEXT NOT NULL UNIQUE,
                government_id TEXT NOT NULL UNIQUE,
                role TEXT NOT NULL,
                password TEXT NOT NULL,
                raw_cv_text TEXT
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
        createJobsTable();
        createSkillsTable();
        createAccommodationsTable();
        createUserSkillsTable();
        createJobSkillsTable();
        createUserAccommodationsTable();
        createJobAccommodationsTable();
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            logger.info("Database connection closed.");
        }
    }

    public Connection getConnection() {
        return connection;
    }
}