package com.crown.clinics.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MedicalRecordResponseDto(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt,
        String createdBy,
        String patientName,
        List<AttachmentResponseDto> attachments
) {}
