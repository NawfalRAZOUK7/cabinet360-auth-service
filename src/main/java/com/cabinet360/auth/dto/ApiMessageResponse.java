package com.cabinet360.auth.dto;

import org.springframework.http.ResponseEntity;

public class ApiMessageResponse {

    private String message;

    public ApiMessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    // ✅ Static helper to return a ResponseEntity<ApiMessageResponse>
    public static ResponseEntity<ApiMessageResponse> of(String message) {
        return ResponseEntity.ok(new ApiMessageResponse(message));
    }
}
