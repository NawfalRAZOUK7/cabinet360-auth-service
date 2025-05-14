package com.cabinet360.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PhoneVerificationRequest {

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Verification code is required")
    private String code;

    // Getters and Setters
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
