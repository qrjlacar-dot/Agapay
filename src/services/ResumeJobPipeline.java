package services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import model.Job;

public class ResumeJobPipeline {

    private final ResumeParser resumeParser = new ResumeParser();
    private final JobRecommendationServices recommendationService =
            new JobRecommendationServices();

    public String getResumeText(File resumeFile) throws IOException {
        return resumeParser.extractText(resumeFile);
    }

    public List<Job> process(File resumeFile, int userId) throws IOException {
        return processText(getResumeText(resumeFile), userId);
    }

    public List<Job> processText(String resumeText, int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Valid user ID is required.");
        }
        if (resumeText == null || resumeText.isBlank()) {
            throw new IllegalArgumentException("Resume contains no readable text.");
        }

        List<Integer> skillIds = resumeParser.extractSkillIdsFromText(resumeText);
        resumeParser.linkSkillsToUser(userId, skillIds);

        return recommendationService.getRecommendedJobsForUser(userId);
    }

    public List<Job> getRecommendations(int userId) {
        return recommendationService.getRecommendedJobsForUser(userId);
    }
}