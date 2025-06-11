package com.crown.clinics.dto;

public record AttachmentRequestDto(
        String filename,
        String fileType
) {}