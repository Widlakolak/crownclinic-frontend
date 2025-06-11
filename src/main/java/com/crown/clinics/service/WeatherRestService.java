package com.crown.clinics.service;

import com.crown.clinics.dto.WeatherDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherRestService {

    private final WebClient webClient;

    public WeatherRestService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api").build();
    }

    public Mono<WeatherDto> getTodayWeather(String city) {
        return webClient.get()
                .uri("/weather/{city}", city)
                .retrieve()
                .bodyToMono(WeatherDto.class)
                .doOnError(e -> System.err.println("Błąd pobierania pogody dla miasta " + city + ": " + e.getMessage()));
    }
}