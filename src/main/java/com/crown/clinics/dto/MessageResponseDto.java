package com.crown.clinics.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MessageResponseDto(
        Long id,
        String subject,
        String content,
        String senderName,
        List<String> recipientName,
        LocalDateTime sentAt,
        String status,
        List<AttachmentResponseDto> attachments
) {}