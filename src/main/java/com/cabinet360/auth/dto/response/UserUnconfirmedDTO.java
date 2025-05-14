package com.cabinet360.auth.dto.response;

import com.cabinet360.auth.enums.Role;

import java.time.LocalDateTime;

public class UserUnconfirmedDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private LocalDateTime createdAt;
    private boolean approved; // ✅ Spécifique aux docteurs
    private String status;    // ✅ Ajouté pour indiquer le type d’attente (confirmation / approbation)

    public UserUnconfirmedDTO() {}

    public UserUnconfirmedDTO(Long id, String email, String firstName, String lastName, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.createdAt = createdAt;
    }

    public UserUnconfirmedDTO(Long id, String email, String firstName, String lastName, Role role, LocalDateTime createdAt, boolean approved) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.createdAt = createdAt;
        this.approved = approved;
    }

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
