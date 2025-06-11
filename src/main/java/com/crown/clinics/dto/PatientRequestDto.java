package com.crown.clinics.dto;

import java.time.LocalDate;

public record PatientRequestDto(
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate dateOfBirth
) {}