package com.crown.clinics.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class AuthService {

    private static final String TOKEN_KEY = "auth-token";
    private String currentToken; // dodatkowe pole do przechowywania tokenu
    private final WebClient webClient = WebClient.create("http://localhost:8080/api");

    /**
     * Inicjuje sesję przy wejściu na dowolny widok:
     * 1) jeśli w URL jest ?token=… → zapisujemy go i czyścimy URL
     * 2) jeśli w VaadinSession (lub w currentToken) jest token → fetchCurrentUser + navigateAfterLogin
     */
    public void initLogin(UI ui) {
        var loc    = ui.getInternals().getActiveViewLocation();
        var tokens = loc.getQueryParameters().getParameters().getOrDefault("token", List.of());
        if (!tokens.isEmpty()) {
            String token = tokens.get(0);
            saveToken(token);
            ui.getPage().executeJs("localStorage.setItem('auth-token',$0);", token);
            ui.getPage().getHistory().replaceState(null, loc.getPath());
        }
        String token = getToken();
        if (token != null) {
            fetchCurrentUser().subscribe(
                    user -> ui.access(() -> navigateAfterLogin(ui, user)),
                    err  -> ui.access(() -> System.err.println("Błąd inicjalizacji sesji: " + err.getMessage()))
            );
        }
    }

    public void authenticate(UI ui, String username, String password, Runnable onError) {
        login(username, password)
                .subscribe(
                        token -> ui.access(() -> {
                            saveToken(token);
                            ui.getPage().executeJs("localStorage.setItem('auth-token',$0);", token);
                            fetchCurrentUser().subscribe(u -> ui.access(() -> navigateAfterLogin(ui, u)));
                        }),
                        err -> ui.access(onError::run)
                );
    }

    public void updateProfilePartial(UserDto dto,
                                     Runnable onSuccessNoNav,
                                     Consumer<UserDto> onSuccessWithNav,
                                     Consumer<Throwable> onError) {

        Map<String,Object> updates = Map.of(
                "firstName", dto.firstName(),
                "lastName",  dto.lastName(),
                "email",     dto.email(),
                "phone",     dto.phone()
        );

        webClient.mutate()
                .defaultHeader("Authorization", "Bearer " + getToken())
                .build()
                .patch()
                .uri("/users/" + dto.id())
                .bodyValue(updates)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        __ -> {
                            onSuccessNoNav.run();
                            fetchCurrentUser().subscribe(onSuccessWithNav, onError);
                        },
                        onError
                );
    }

    private void saveToken(String token) {
        currentToken = token;                        // zapisujemy w polu
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(TOKEN_KEY, token);
        }
    }

    public String getToken() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            String token = (String) session.getAttribute(TOKEN_KEY);
            if (token != null) {
                currentToken = token;              // synchronizacja pola
                return token;
            }
        }
        return currentToken;
    }

    public WebClient authenticatedClient() {
        return webClient.mutate()
                .defaultHeader("Authorization", "Bearer " + getToken())
                .build();
    }

    public Mono<UserDto> fetchCurrentUser() {
        return webClient.get().uri("/users/me")
                .headers(h -> h.setBearerAuth(getToken()))
                .retrieve().bodyToMono(UserDto.class);
    }

    public void logout(UI ui) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(TOKEN_KEY, null);
        }
        currentToken = null;
        ui.getPage().executeJs("localStorage.removeItem('auth-token');");
        ui.navigate("login");
    }

    public void navigateAfterLogin(UI ui, UserDto user) {
        if (user.firstName() == null || user.lastName() == null || user.phone() == null) {
            ui.navigate("profile");
        } else {
            switch (user.role()) {
                case "RECEPTIONIST" -> ui.navigate("reception");
                case "ADMIN"        -> ui.navigate("admin");
                default             -> ui.navigate("doctor");
            }
        }
    }

    private Mono<String> login(String u, String p) {
        return webClient.post().uri("/auth/login")
                .bodyValue(new LoginRequest(u,p))
                .retrieve().bodyToMono(TokenResponse.class)
                .map(TokenResponse::token);
    }

    public record LoginRequest(String username, String password) {}
    public record TokenResponse(String token) {}
    public record UserDto(
            Long id, String firstName, String lastName,
            String email, String phone, String googleCalendarId, String role
    ) {}
}