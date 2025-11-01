package com.example.gallerycart.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;
import java.util.List;

@Entity(tableName = "user",
        indices = {@Index(value = "username", unique = true),
                @Index(value = "email", unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username;
    private String email;
    private String role; // customer, artist, admin
    private String password; // bcrypt hashed (chắc vậy)
    private Date userDob;
    private String phoneNumber;
    private String userAvatar;
    private Date createdDate;
    private boolean isBanned;
    private boolean isArtist;
    private String professionSummary;
    private List<String> skills;
    private List<String> software;
    private String contactInfo;
    private int commissionStatus; // 0/1/2 aka CLOSE/OPEN/FULL (todo: discuss this)

    public User() {
        this.createdDate = new Date();
        this.isBanned = false;
        this.isArtist = false;
        this.commissionStatus = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Date getUserDob() { return userDob; }
    public void setUserDob(Date userDob) { this.userDob = userDob; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { isBanned = banned; }

    public boolean isArtist() { return isArtist; }
    public void setArtist(boolean artist) { isArtist = artist; }

    public String getProfessionSummary() { return professionSummary; }
    public void setProfessionSummary(String professionSummary) {
        this.professionSummary = professionSummary;
    }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getSoftware() { return software; }
    public void setSoftware(List<String> software) { this.software = software; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public int getCommissionStatus() { return commissionStatus; }
    public void setCommissionStatus(int commissionStatus) {
        this.commissionStatus = commissionStatus;
    }
}