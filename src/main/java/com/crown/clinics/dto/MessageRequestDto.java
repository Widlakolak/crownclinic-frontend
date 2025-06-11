package com.crown.clinics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MessageRequestDto(
        @NotNull(message = "senderId nie może być nullem")
        Long senderId,

        @NotEmpty(message = "recipientIds nie może być puste")
        List<@NotNull(message = "Id odbiorcy nie może być nullem") Long> recipientIds,

        @NotBlank(message = "subject nie może być pusty")
        String subject,

        @NotBlank(message = "content nie może być pusty")
        String content
) {}