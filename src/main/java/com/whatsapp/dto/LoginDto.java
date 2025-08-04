package com.whatsapp.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDto {
    
    @NotBlank(message = "Email or phone number is required")
    private String identifier; // Can be email or phone number
    
    @NotBlank(message = "Password is required")
    private String password;
    
    // Constructors
    public LoginDto() {}
    
    public LoginDto(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }
    
    // Getters and Setters
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}