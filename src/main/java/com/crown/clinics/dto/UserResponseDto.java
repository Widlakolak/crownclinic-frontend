package com.crown.clinics.dto;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String googleCalendarId,
        String role
) {}