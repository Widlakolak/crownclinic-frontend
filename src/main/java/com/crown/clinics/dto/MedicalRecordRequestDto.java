package com.crown.clinics.dto;

public record MedicalRecordRequestDto(
        String title,
        String description,
        Long patientId,
        Long createdById
) {}