package model;

import java.util.ArrayList;
import java.util.List;

public class Job {
    private int jobId;
    private String title;
    private String employerName;
    private String description;
    private String location;
    private String contactNumber;
    private String payInfo;
    private String scheduleInfo;

    
    private List<String> skills;
    private List<String> accommodations;
    private double matchScore;

    // Database record constructor
    public Job(int jobId, String title, String employerName, String description, 
               String location, String contactNumber, String payInfo, String scheduleInfo) {
        this.jobId = jobId;
        this.title = title;
        this.employerName = employerName;
        this.description = description;
        this.location = location;
        this.contactNumber = contactNumber;
        this.payInfo = payInfo;
        this.scheduleInfo = scheduleInfo;
        this.skills = new ArrayList<>();
        this.accommodations = new ArrayList<>();
    }

    // Default constructor
    public Job() {
        this.skills = new ArrayList<>();
        this.accommodations = new ArrayList<>();
    }

    // Database column Getters and Setters
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEmployerName() { return employerName; }
    public void setEmployerName(String employerName) { this.employerName = employerName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getPayInfo() { return payInfo; }
    public void setPayInfo(String payInfo) { this.payInfo = payInfo; }

    public String getScheduleInfo() { return scheduleInfo; }
    public void setScheduleInfo(String scheduleInfo) { this.scheduleInfo = scheduleInfo; }

    // Relational/Joined list Getters and Setters
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getAccommodations() { return accommodations; }
    public void setAccommodations(List<String> accommodations) { this.accommodations = accommodations; }

    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
}