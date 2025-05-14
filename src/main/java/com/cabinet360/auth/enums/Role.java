package com.cabinet360.auth.enums;

public enum Role {
    DOCTOR,
    ASSISTANT,
    PATIENT,
    ADMIN // ✅ Needed for @PreAuthorize checks
}
