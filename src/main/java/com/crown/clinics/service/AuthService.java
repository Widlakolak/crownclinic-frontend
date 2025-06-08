package com.crown.clinics.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AuthService {

    private static final String TOKEN_KEY = "auth-token";
    private final WebClient webClient = WebClient.create("http://localhost:8080/api");

    public void saveToken(String token) {
        VaadinSession.getCurrent().setAttribute(TOKEN_KEY, token);
    }

    public String getToken() {
        return (String) VaadinSession.getCurrent().getAttribute(TOKEN_KEY);
    }

    public void clearToken() {
        VaadinSession.getCurrent().setAttribute(TOKEN_KEY, null);
    }

    public Mono<UserDto> fetchCurrentUser() {
        return webClient.get()
                .uri("/users/me")
                .headers(h -> h.setBearerAuth(getToken()))
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public WebClient authenticatedClient() {
        String token = getToken();
        if (token == null) {
            throw new RuntimeException("Brak tokenu w sesji");
        }

        return webClient.mutate()
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    public void logout() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(TOKEN_KEY, null);
            session.close();
        }

        UI.getCurrent().getPage().executeJs("localStorage.removeItem('token');");

        UI.getCurrent().navigate("login");
    }

    public void handleTokenFromUrl(String token, UI ui) {
        saveToken(token);
        fetchCurrentUser().subscribe(user -> {
            ui.access(() -> ui.navigate(getRedirectRouteForRole(user.role())));
        });
    }

    public Mono<String> login(String username, String password) {
        return webClient.post()
                .uri("/auth/login")
                .bodyValue(new LoginRequest(username, password))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::token);
    }

    public void handleSuccessfulLogin(String token, UI ui) {
        saveToken(token);
        fetchCurrentUser().subscribe(user -> {
            ui.access(() -> {
                ui.navigate(getRedirectRouteForRole(user.role()));
            });
        });
    }

    public void handleLoginViewAttach(UI ui) {
        var location = ui.getInternals().getActiveViewLocation();
        List<String> tokens = location.getQueryParameters().getParameters().getOrDefault("token", List.of());
        String token = tokens.isEmpty() ? null : tokens.get(0);
        System.out.println("TOKEN = " + token);

        if (token != null) {
            handleTokenFromUrl(token, ui);
            ui.getPage().getHistory().replaceState(null, location.getPath());
            return;
        }

        if (isLoggedIn()) {
            ui.navigate("dashboard");
        }

        System.out.println("ON ATTACH: path=" + location.getPath() + ", query=" + location.getQueryParameters());
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isBlank();
    }

    private String getRedirectRouteForRole(String role) {
        return switch (role) {
            case "DOCTOR" -> "doctor";
            case "RECEPTIONIST" -> "reception";
            default -> "profile";
        };
    }

    public record LoginRequest(String username, String password) {
    }

    public record TokenResponse(String token) {
    }

    public record UserDto(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone,
            String googleCalendarId,
            String role
    ) {
    }
}