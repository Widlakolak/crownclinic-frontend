package com.crown.clinics.view;

import com.crown.clinics.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.reactive.function.client.WebClient;

@Route("profile")
@PageTitle("Profile")
@PermitAll
public class ProfileCheckView extends VerticalLayout {

    private final TextField firstName = new TextField("Imię");
    private final TextField lastName = new TextField("Nazwisko");
    private final EmailField email = new EmailField("E-mail");
    private final TextField phone = new TextField("Telefon");
    private final Button saveButton = new Button("Zapisz dane");

    private final AuthService authService;
    private AuthService.UserDto currentUser;

    public ProfileCheckView(AuthService authService) {
        this.authService = authService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("drawer-login");

        H2 header = new H2("Uzupełnij swój profil");
        header.getStyle()
                .set("font-family", "'Courier New', monospace")
                .set("color", "#444");

        FormLayout form = new FormLayout(firstName, lastName, email, phone);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("400px", 2)
        );

        saveButton.addClickListener(e -> saveUserData());

        add(header, form, saveButton);

        loadUserData();
    }

    private void loadUserData() {
        authService.fetchCurrentUser().subscribe(user -> {
            this.currentUser = user;
            getUI().ifPresent(ui -> ui.access(() -> {
                firstName.setValue(user.firstName() != null ? user.firstName() : "");
                lastName.setValue(user.lastName() != null ? user.lastName() : "");
                email.setValue(user.email() != null ? user.email() : "");
                phone.setValue(user.phone() != null ? user.phone() : "");
            }));
        });
    }

    private void saveUserData() {
        WebClient client = authService.authenticatedClient();
        client.put()
                .uri("/users/" + currentUser.id())
                .bodyValue(new AuthService.UserDto(
                        currentUser.id(),
                        firstName.getValue(),
                        lastName.getValue(),
                        email.getValue(),
                        phone.getValue(),
                        currentUser.googleCalendarId(),
                        currentUser.role()
                ))
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> {
                            Notification.show("Zapisano dane profilu");
                            UI.getCurrent().navigate(getTargetRoute(currentUser.role()));
                        },
                        error -> Notification.show("Nie udało się zapisać danych")
                );
    }

    private String getTargetRoute(String role) {
        return switch (role) {
            case "DOCTOR" -> "doctor";
            case "RECEPTIONIST" -> "reception";
            default -> "login";
        };
    }
}
