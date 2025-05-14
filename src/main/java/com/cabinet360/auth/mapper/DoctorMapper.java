package com.cabinet360.auth.mapper;

import com.cabinet360.auth.dto.DoctorResponseDTO;
import com.cabinet360.auth.entity.DoctorUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DoctorMapper {

    public DoctorResponseDTO toDto(DoctorUser doctor) {
        DoctorResponseDTO dto = new DoctorResponseDTO();
        dto.setId(doctor.getId());
        dto.setEmail(doctor.getEmail());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setSpeciality(doctor.getSpeciality());
        dto.setMedicalLicenseNumber(doctor.getMedicalLicenseNumber());
        dto.setConfirmed(doctor.isConfirmed()); // champ hérité de AuthUser
        dto.setApproved(doctor.isApproved());   // champ hérité de AuthUser
        dto.setCreatedAt(doctor.getCreatedAt());
        dto.setApprovedAt(doctor.getApprovedAt());
        return dto;
    }

    public List<DoctorResponseDTO> toDtoList(List<DoctorUser> doctors) {
        return doctors.stream().map(this::toDto).collect(Collectors.toList());
    }
}
