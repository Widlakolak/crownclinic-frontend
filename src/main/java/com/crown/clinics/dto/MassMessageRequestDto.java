package com.crown.clinics.dto;

import java.util.List;

public record MassMessageRequestDto(
        Long senderId,
        List<Long> recipientIds,
        String subject,
        String content
) {}
