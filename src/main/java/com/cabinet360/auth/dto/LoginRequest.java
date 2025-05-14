package com.cabinet360.auth.dto;

public class LoginRequest {
    private String email;
    private String password;
    // Optional 2FA code for doctors
    private String twoFACode;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTwoFACode() { return twoFACode; }
    public void setTwoFACode(String twoFACode) { this.twoFACode = twoFACode; }

}
