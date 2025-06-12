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
        Long doctorId,
        String patientFullName,
        String patientEmail,
        Long patientId,
        String googleEventId
) {}
