package com.cabinet360.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public class TwoFAVerifyRequest {

    @NotBlank
    private String email;

    private int code;

    // Getters & Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
}
