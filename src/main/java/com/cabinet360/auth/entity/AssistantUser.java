package com.cabinet360.auth.entity;

import com.cabinet360.auth.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
        name = "assistant_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_assistant_email", columnNames = "email")
        }
)
public class AssistantUser extends AuthUser {

    @Column(nullable = true)
    private Long supervisorId;

    @Embedded
    @NotNull
    private CabinetFields cabinet;

    public AssistantUser() {
        this.role = Role.ASSISTANT;
        this.requiresApproval = true;
        this.isApproved = false;
    }

    // Getters and Setters
    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }

    public CabinetFields getCabinet() { return cabinet; }
    public void setCabinet(CabinetFields cabinet) { this.cabinet = cabinet; }
}
