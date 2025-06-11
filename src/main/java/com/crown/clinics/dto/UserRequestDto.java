package com.crown.clinics.dto;

public record UserRequestDto(
        String firstName,
        String lastName,
        String email,
        String phone,
        String googleCalendarId,
        String role
) {}
