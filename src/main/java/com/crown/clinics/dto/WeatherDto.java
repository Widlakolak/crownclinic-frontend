package com.crown.clinics.dto;

public record WeatherDto(
        Double temperature,
        String description,
        String icon
) {}