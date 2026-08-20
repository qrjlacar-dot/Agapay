package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import database.DatabaseManager;
import model.Job;

public class JobRecommendationServices {
    
    private final DatabaseManager dbManager = new DatabaseManager();
    private final ResumeParser resumeParser = new ResumeParser();
    
    public List<Job> getRecommendedJobsForUser(int userId) {
        List<Job> allJobs = fetchAllJobs();

        
        List<Integer> userSkills = fetchTraits(userId, "userSkills", "user_id", "skill_id");
        
        
        List<Integer> userAccoms = fetchTraits(userId, "user_accommodations", "user_id", "accommodation_id");
        
        for (Job job : allJobs) {
            List<Integer> jobSkills = fetchTraits(job.getJobId(), "job_skills", "job_id", "skill_id");
            if (jobSkills.isEmpty()) {
                String jobText = job.getTitle() + " " + job.getDescription();
                jobSkills = resumeParser.extractSkillIdsFromText(jobText);
            }
            
            
            List<Integer> jobAccoms = fetchTraits(job.getJobId(), "job_accommodations", "job_id", "accommodation_id");

            
            double matchScore = calculateMatchScore(userSkills, userAccoms, jobSkills, jobAccoms);
            job.setMatchScore(matchScore);
        }

        // Sort descending (highest match score first)
        allJobs.sort((j1, j2) -> Double.compare(j2.getMatchScore(), j1.getMatchScore()));

        return allJobs;
    }

    public List<Job> searchRecommendedJobsForUser(int userId, String query) {
        List<Job> recommended = getRecommendedJobsForUser(userId);
        if (query == null || query.isBlank()) {
            return recommended;
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        List<Job> filtered = new ArrayList<>();

        for (Job job : recommended) {
            if (containsIgnoreCase(job.getTitle(), normalizedQuery)
                || containsIgnoreCase(job.getEmployerName(), normalizedQuery)
                || containsIgnoreCase(job.getLocation(), normalizedQuery)
                || containsIgnoreCase(job.getDescription(), normalizedQuery)) {
                filtered.add(job);
            }
        }

        return filtered;
    }

    private boolean containsIgnoreCase(String value, String normalizedQuery) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }

    // This is where the moments turn into memories.
    private double calculateMatchScore(List<Integer> userSkills, List<Integer> userAccoms, 
                                       List<Integer> jobSkills, List<Integer> jobAccoms) {
        
        int totalRequirements = jobSkills.size() + jobAccoms.size();
        
        // A job without stored requirements cannot be verified as a match.
        if (totalRequirements == 0) return 0.0;

        int matchCount = 0;

        // Check for skill overlaps
        for (Integer requiredSkill : jobSkills) {
            if (userSkills.contains(requiredSkill)) {
                matchCount++;
            }
        }

        // Check for accommodation overlaps
        for (Integer providedAccom : jobAccoms) {
            if (userAccoms.contains(providedAccom)) {
                matchCount++;
            }
        }

        return ((double) matchCount / totalRequirements) * 100.0;
    }

    // Helper Functions that are really helpful
    private List<Job> fetchAllJobs() {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs";
        Connection conn = dbManager.getConnection();

        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery();) {

            while (rs.next()) {
                Job job = new Job(
                    rs.getInt("job_id"),
                    rs.getString("title"),
                    rs.getString("employer_name"),
                    rs.getString("description"),
                    rs.getString("location"),
                    rs.getString("contact_number"),
                    rs.getString("pay_info"),
                    rs.getString("schedule_info")
                );
                jobs.add(job);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jobs;
    }

    private List<Integer> fetchTraits(int entityId, String tableName, String whereColumn, String selectColumn) {
        List<Integer> traits = new ArrayList<>();
        String sql = "SELECT " + selectColumn + " FROM " + tableName + " WHERE " + whereColumn + " = ?";
        Connection conn = dbManager.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entityId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    traits.add(rs.getInt(selectColumn));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch traits from " + tableName + ": " + e.getMessage());
        }
        return traits;
    }
}