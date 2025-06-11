package com.crown.clinics.dto;

public record AttachmentResponseDto(
        Long id,
        String filename,
        String fileType,
        String attachmentType
) {}
