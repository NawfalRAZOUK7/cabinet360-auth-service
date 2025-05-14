package com.cabinet360.auth.dto;

public class ApproveDoctorRequest {
    private Long doctorId;
    private boolean approve;

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public boolean isApprove() { return approve; }
    public void setApprove(boolean approve) { this.approve = approve; }
}
