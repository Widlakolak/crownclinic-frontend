package com.crown.clinics.view;

import com.crown.clinics.dto.UserResponseDto;
import com.crown.clinics.service.AuthService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("profile")
@PageTitle("Profile")
@AnonymousAllowed
public class ProfileCheckView extends VerticalLayout {

    private final AuthService authService;
    private final TextField    firstName  = new TextField("Imię");
    private final TextField    lastName   = new TextField("Nazwisko");
    private final EmailField   email      = new EmailField("E-mail");
    private final TextField    phone      = new TextField("Telefon");
    private final Button       saveButton = new Button("Zapisz dane");
    private final ProgressBar  loadingBar = new ProgressBar();

    private UI ui;
    private UserResponseDto currentUser;

    public ProfileCheckView(AuthService authService) {
        this.authService = authService;
        configureLayout();
        configureForm();
    }

    private void configureLayout() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("drawer-login");

        H2 header = new H2("Uzupełnij swój profil");
        header.getStyle()
                .set("font-family", "'Courier New', monospace")
                .set("color", "#444");
        add(header);
    }

    private void configureForm() {
        // FormLayout z int responsive step
        FormLayout form = new FormLayout(firstName, lastName, email, phone);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0",   1),
                new FormLayout.ResponsiveStep("400px",2)
        );

        // przycisk i pasek ładowania
        saveButton.addClickListener(e -> saveUserData());
        loadingBar.setIndeterminate(true);
        loadingBar.setVisible(false);

        add(form, saveButton, loadingBar);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.ui = attachEvent.getUI();

        authService.initLogin(ui);

        authService.fetchCurrentUser().subscribe(
                user -> {
                    this.currentUser = user;
                    ui.access(() -> {
                        firstName.setValue(nullToEmpty(user.firstName()));
                        lastName .setValue(nullToEmpty(user.lastName()));
                        email    .setValue(nullToEmpty(user.email()));
                        phone    .setValue(nullToEmpty(user.phone()));
                    });
                },
                err -> ui.access(() ->
                        Notification.show("Nie udało się pobrać profilu: " + err.getMessage())
                )
        );
    }

    private void saveUserData() {
        // blokada UI
        saveButton.setEnabled(false);
        saveButton.setText("Zapisywanie...");
        loadingBar.setVisible(true);

        var dto = new UserResponseDto(
                currentUser.id(),
                firstName.getValue(),
                lastName.getValue(),
                email.getValue(),
                phone.getValue(),
                currentUser.googleCalendarId(),
                currentUser.role()
        );

        authService.updateProfilePartial(
                dto,
                () -> ui.access(() -> {
                    Notification.show("Zapisano dane profilu");
                    loadingBar.setVisible(false);
                }),
                user -> ui.access(() ->
                        authService.navigateAfterLogin(ui, user)),
                err -> ui.access(() -> {
                    Notification.show("Błąd zapisu: " + err.getMessage());
                    saveButton.setEnabled(true);
                    saveButton.setText("Zapisz dane");
                    loadingBar.setVisible(false);
                })
        );
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}