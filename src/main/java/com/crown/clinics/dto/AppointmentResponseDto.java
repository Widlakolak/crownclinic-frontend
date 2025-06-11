package com.crown.clinics.dto;

import java.time.ZonedDateTime;

public record AppointmentResponseDto(
        Long id,
        ZonedDateTime startDateTime,
        ZonedDateTime endDateTime,
        String notes,
        String status,
        String doctorFullName,
        String doctorEmail,
        String patientFullName,
        String patientEmail,
        String googleEventId
) {}
