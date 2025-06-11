package com.crown.clinics.dto;

import java.util.List;

public record MessageRequestDto(
        Long senderId,
        List<Long> recipientIds,
        String subject,
        String content
) {}