package com.crown.clinics.service;

import com.crown.clinics.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BackendService {

    private final String backendUrl;
    private final AuthService authService;

    public BackendService(@Value("${backend.url}") String backendUrl, AuthService authService) {
        this.backendUrl = backendUrl;
        this.authService = authService;
    }

    private WebClient getClient() {
        String token = authService.getToken();
        WebClient.Builder builder = WebClient.builder().baseUrl(backendUrl);
        if (token != null && !token.isEmpty()) {
            builder.defaultHeader("Authorization", "Bearer " + token);
        }
        return builder.build();
    }

    public List<AppointmentResponseDto> getAppointments() {
        return getClient().get().uri("/appointments").retrieve()
                .bodyToFlux(AppointmentResponseDto.class).collectList().block();
    }

    public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {
        return getClient().post().uri("/appointments")
                .body(Mono.just(dto), AppointmentRequestDto.class).retrieve()
                .bodyToMono(AppointmentResponseDto.class).block();
    }

    public AppointmentResponseDto updateAppointment(Long id, AppointmentRequestDto dto) {
        return getClient().put().uri("/appointments/" + id)
                .body(Mono.just(dto), AppointmentRequestDto.class).retrieve()
                .bodyToMono(AppointmentResponseDto.class).block();
    }

    public void deleteAppointment(Long id) {
        getClient().delete().uri("/appointments/" + id).retrieve()
                .toBodilessEntity().block();
    }

    public List<PatientResponseDto> getPatients() {
        return getClient().get().uri("/api/patients").retrieve()
                .bodyToFlux(PatientResponseDto.class).collectList().block();
    }

    public List<UserResponseDto> getDoctors() {
        return getClient().get().uri("/api/users").retrieve()
                .bodyToFlux(UserResponseDto.class).collectList().block().stream()
                .filter(user -> "DOCTOR".equalsIgnoreCase(user.role()))
                .collect(Collectors.toList());
    }
}