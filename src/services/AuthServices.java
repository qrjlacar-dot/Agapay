package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import database.DatabaseManager;

public class AuthServices {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z]).{8,}$");

    public static int activeUserId = -1;

    private DatabaseManager dbManager = new DatabaseManager();

    public boolean login(String email, String password) {
        if (email == null || password == null || email.isBlank()) {
            return false;
        }
        int userId = getUserIdOnLogin(email, password);

        if (userId != -1) {
            activeUserId = userId;
            return true;
        }

        return false;

    }  
        

    public boolean register(String name, String email, String govId, String password, String confirmPassword, String rawCvText) {

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            System.out.println("Error: Fields cannot be empty.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            System.out.println("Error: Passwords do not match.");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            System.out.println("Error: Invalid email format.");
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            System.out.println("Error: Password must be 8+ characters with 1 uppercase and 1 number.");
            return false;
        }

        
        int newUserId = registerUserAndGetId(name, email, govId, password, rawCvText);

        if (newUserId != -1) {
            activeUserId = newUserId;
            return true;
        }

        return false;
    }



    // Helper Functions if needed.. 
    public int getUserIdOnLogin(String email, String password) {
        

        int userId = -1;

        String sql = "SELECT user_id FROM userAccount WHERE email = ? AND password = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                }
            } 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    public int registerUserAndGetId(String name, String email, String govId, String password, String raw_cv_text) {
        int generatedUserId = -1;

        String sql = "INSERT INTO userAccount(name, email, government_id, role, password, raw_cv_text) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection(); PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, govId);
            statement.setString(4, "USER");
            statement.setString(5, password); 
            statement.setString(6, raw_cv_text);
            
            statement.executeUpdate();
            
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedUserId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedUserId;
    }

}
