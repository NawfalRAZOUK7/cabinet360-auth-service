package com.cabinet360.auth.mapper;

import com.cabinet360.auth.dto.response.UserUnconfirmedDTO;
import com.cabinet360.auth.entity.AuthUser;
import com.cabinet360.auth.entity.DoctorUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserUnconfirmedMapper {

    public UserUnconfirmedDTO toDto(AuthUser user) {
        UserUnconfirmedDTO dto = new UserUnconfirmedDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());

        // Spécifique aux docteurs : approuvé ou non
        if (user instanceof DoctorUser doctor) {
            dto.setApproved(doctor.isApproved());

            // Déduire le statut personnalisé
            if (!doctor.isConfirmed() && !doctor.isApproved()) {
                dto.setStatus("pending_confirmation_and_approval");
            } else if (!doctor.isConfirmed()) {
                dto.setStatus("pending_confirmation");
            } else if (!doctor.isApproved()) {
                dto.setStatus("pending_approval");
            } else {
                dto.setStatus("active");
            }
        } else {
            // Pour les autres rôles (Assistant, Patient, Admin)
            dto.setStatus(!user.isConfirmed() ? "pending_confirmation" : "active");
        }

        return dto;
    }

    public List<UserUnconfirmedDTO> toDtoList(List<? extends AuthUser> users) {
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
