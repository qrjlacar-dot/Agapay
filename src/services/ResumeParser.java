package services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import database.DatabaseManager;

public class ResumeParser {

    private final DatabaseManager dbManager = new DatabaseManager();

    // Map common aliases/variations to their canonical master skill names
    private static final Map<String, List<String>> SKILL_ALIASES = new HashMap<>();

    static {
        SKILL_ALIASES.put("communication", Arrays.asList("oral communication", "written communication", "verbal communication", "public speaking", "interpersonal"));
        SKILL_ALIASES.put("administration", Arrays.asList("admin", "administrative", "office assistant", "clerical", "data entry", "documentation"));
        SKILL_ALIASES.put("customer service", Arrays.asList("customer support", "client support", "customer care", "help desk", "front desk"));
        SKILL_ALIASES.put("video editing", Arrays.asList("video editor", "premiere pro", "after effects", "davinci", "final cut", "multimedia"));
        SKILL_ALIASES.put("it support", Arrays.asList("tech support", "technical support", "desktop support", "troubleshooting", "hardware", "software"));
        SKILL_ALIASES.put("counseling", Arrays.asList("counselor", "psychology", "mental health", "guidance", "therapy"));
    }

    public String extractText(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist.");
        }

        String fileName = file.getName().toLowerCase(Locale.ROOT);

        if (fileName.endsWith(".pdf")) {
            return extractTextFromPdf(file); 
        } else if (fileName.endsWith(".docx")) {
            return extractTextFromDocx(file);
        } else {
            throw new UnsupportedOperationException("Unsupported file type. Please upload a PDF or DOCX.");
        }
    }

    private String extractTextFromPdf(File file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file)) {
            if (document.isEncrypted()) {
                System.err.println("PDF is encrypted and cannot be read.");
                return ""; 
            }
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document).trim();
        }
    }

    private String extractTextFromDocx(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText().trim();
        }
    }

    public List<Integer> extractSkillIdsFromText(String rawText) {
        List<Integer> matchedSkillIds = new ArrayList<>();
        if (rawText == null || rawText.isBlank()) {
            return matchedSkillIds;
        }

        String normalizedText = normalizeForMatching(rawText);
        String sql = "SELECT skill_id, name FROM masterSkills"; 
        
        // Grab connection outside of try-with-resources to keep it alive
        Connection conn = dbManager.getConnection();

        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                int skillId = rs.getInt("skill_id");
                String skillName = rs.getString("name");
                if (skillName == null || skillName.isBlank()) {
                    continue;
                }

                String canonicalName = skillName.trim().toLowerCase(Locale.ROOT);
                List<String> keywords = new ArrayList<>();
                keywords.add(canonicalName);

                // Add configured aliases/synonyms if available
                if (SKILL_ALIASES.containsKey(canonicalName)) {
                    keywords.addAll(SKILL_ALIASES.get(canonicalName));
                }

                for (String keyword : keywords) {
                    String normalizedKeyword = normalizeForMatching(keyword).trim();
                    Pattern pattern = Pattern.compile(
                            "(?<![a-z0-9])" + Pattern.quote(normalizedKeyword)
                                    + "(?![a-z0-9])");
                    if (pattern.matcher(normalizedText).find()) {
                        matchedSkillIds.add(skillId);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchedSkillIds;
    }

    private String normalizeForMatching(String value) {
        return " " + value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ") + " ";
    }

    public void linkSkillsToUser(int userId, List<Integer> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) { 
            return; 
        }
        
        String sql = "INSERT OR IGNORE INTO userSkills (user_id, skill_id) VALUES (?, ?)";


        Connection conn = dbManager.getConnection();

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            
            for (int skillId : skillIds) {
                statement.setInt(1, userId);
                statement.setInt(2, skillId);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}