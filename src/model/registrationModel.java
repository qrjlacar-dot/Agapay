package model;

import java.io.File;
import java.util.List;

public class registrationModel {

    private String firstName;
    private String lastName;
    private String username;
    private String contactNumber;
    private String governmentId;
    private String role;
    private String password;
    private List<Integer> selectedAccomodationId;
    private File uploadedResume;

    
    public registrationModel(String firstname, String lastName, String username, String contactNumber, String governmentId,String role, String password, List<Integer> selectedAccomodationId, File uploadedResume) {

        this.firstName = firstname;
        this.lastName = lastName;
        this.username = username;
        this.contactNumber = contactNumber;
        this.governmentId = governmentId;
        this.role = role;
        this.password = password;
        this.selectedAccomodationId = selectedAccomodationId;
        this.uploadedResume = uploadedResume;
        
    }    

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public String getUsername() {
        return username;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    public String getGovernmentId() {
        return governmentId;
    }
    public String getRole() {
        return role;
    }
    public String getPassword() {
        return password;
    }

    public List<Integer> getSelectedAccomodationId() {
        return selectedAccomodationId;
    }

    public File getUploadedResume() {
        return uploadedResume;
    }
}
