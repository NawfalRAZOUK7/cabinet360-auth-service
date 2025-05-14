package com.cabinet360.auth.dto.register;

import com.cabinet360.auth.enums.Role;
import jakarta.validation.constraints.NotNull;

public class AssistantRegisterRequest extends AbstractRegisterRequest {
    @NotNull(message = "Supervisor ID is required")
    private Long supervisorId;

    // Cabinet fields
    private String officeAddress;
    private String fixedPhoneNumber;
    private String workSchedule;

    @Override
    public Role getRole() {
        return Role.ASSISTANT;
    }

    // Getters and Setters
    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }

    public String getOfficeAddress() { return officeAddress; }
    public void setOfficeAddress(String officeAddress) { this.officeAddress = officeAddress; }

    public String getFixedPhoneNumber() { return fixedPhoneNumber; }
    public void setFixedPhoneNumber(String fixedPhoneNumber) { this.fixedPhoneNumber = fixedPhoneNumber; }

    public String getWorkSchedule() { return workSchedule; }
    public void setWorkSchedule(String workSchedule) { this.workSchedule = workSchedule; }
}
