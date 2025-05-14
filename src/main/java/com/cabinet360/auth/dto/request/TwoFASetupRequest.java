package com.cabinet360.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class TwoFASetupRequest {

    @NotBlank
    @Email
    private String email;

    // Getter & Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
