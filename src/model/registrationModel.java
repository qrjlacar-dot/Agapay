package model;

import java.io.File;
import java.util.List;

public class registrationModel {

    private String username;
    private String governmentId;
    private String role;
    private String password;
    private List<Integer> selectedAccomodationId;
    private File uploadedResume;

    public registrationModel() {
    }

    public registrationModel(String username, String governmentId, String role, 
                             String password, List<Integer> selectedAccomodationId, 
                             File uploadedResume) {
        this.username = username;
        this.governmentId = governmentId;
        this.role = role;
        this.password = password;
        this.selectedAccomodationId = selectedAccomodationId;
        this.uploadedResume = uploadedResume;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGovernmentId() {
        return governmentId;
    }

    public void setGovernmentId(String governmentId) {
        this.governmentId = governmentId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Integer> getSelectedAccomodationId() {
        return selectedAccomodationId;
    }

    public void setSelectedAccomodationId(List<Integer> selectedAccomodationId) {
        this.selectedAccomodationId = selectedAccomodationId;
    }

    public File getUploadedResume() {
        return uploadedResume;
    }

    public void setUploadedResume(File uploadedResume) {
        this.uploadedResume = uploadedResume;
    }
}