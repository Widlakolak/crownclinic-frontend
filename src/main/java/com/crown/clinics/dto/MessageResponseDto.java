package com.crown.clinics.dto;

import com.crown.backend.domain.Message;

import java.time.LocalDateTime;
import java.util.List;

public record MessageResponseDto(
        Long id,
        String subject,
        String content,
        String senderName,
        List<String> recipientName,
        LocalDateTime sentAt,
        Message.MessageStatus status,
        List<AttachmentResponseDto> attachments
) {}