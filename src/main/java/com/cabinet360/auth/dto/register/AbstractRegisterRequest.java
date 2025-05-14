package com.cabinet360.auth.dto.register;

import com.cabinet360.auth.enums.Role;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DoctorRegisterRequest.class, name = "DOCTOR"),
        @JsonSubTypes.Type(value = AssistantRegisterRequest.class, name = "ASSISTANT"),
        @JsonSubTypes.Type(value = PatientRegisterRequest.class, name = "PATIENT")
})
public abstract class AbstractRegisterRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    protected String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    protected String password;

    @NotBlank(message = "First name is required")
    protected String firstName;

    @NotBlank(message = "Last name is required")
    protected String lastName;

    public abstract Role getRole();

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
