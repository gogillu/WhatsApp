package com.whatsapp.dto;

import com.whatsapp.entity.User;
import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String profilePicture;
    private String status;
    private User.UserStatus userStatus;
    private LocalDateTime lastSeen;
    
    // Constructors
    public UserDto() {}
    
    public UserDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.id = user.getId();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.fullName = user.getFullName();
        this.profilePicture = user.getProfilePicture();
        this.status = user.getStatus();
        this.userStatus = user.getUserStatus();
        this.lastSeen = user.getLastSeen();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public User.UserStatus getUserStatus() {
        return userStatus;
    }
    
    public void setUserStatus(User.UserStatus userStatus) {
        this.userStatus = userStatus;
    }
    
    public LocalDateTime getLastSeen() {
        return lastSeen;
    }
    
    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }
}