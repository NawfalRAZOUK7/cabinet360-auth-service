package com.cabinet360.auth.dto.request;

import jakarta.validation.constraints.*;

public class ResetPasswordRequest {

    @NotBlank(message = "Reset token is required")
    private String resetToken;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;


    // Getters & Setters
    public String getResetToken() { return resetToken; }

    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
