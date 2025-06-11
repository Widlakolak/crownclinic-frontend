package com.crown.clinics.dto;

import java.time.ZonedDateTime;

public record AppointmentRequestDto(
        ZonedDateTime startDateTime,
        ZonedDateTime endDateTime,
        String notes,
        Long patientId,
        Long doctorId,
        String status
) {}
