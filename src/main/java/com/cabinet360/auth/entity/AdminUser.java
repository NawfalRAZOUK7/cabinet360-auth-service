package com.cabinet360.auth.entity;

import com.cabinet360.auth.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_users")
public class AdminUser extends AuthUser {

    public AdminUser() {
        this.role = Role.ADMIN;
        this.isConfirmed = true;
        this.isApproved = true;
        this.requiresApproval = false;
        this.isActive = true;
    }

    // No extra fields — only inherits from AuthUser
}
