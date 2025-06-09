package com.crown.clinics.view;

import com.crown.clinics.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dependency.CssImport;
import org.springframework.beans.factory.annotation.Autowired;

@Route("login")
@PageTitle("Logowanie")
@CssImport("./styles/styles.css")
public class LoginView extends VerticalLayout {

    private final AuthService auth;
    private final LoginForm loginForm = new LoginForm();

    @Autowired
    public LoginView(AuthService authService) {
        this.auth = authService;

        // Standardowy formularz logowania
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.addLoginListener(e ->
                auth.authenticate(
                        UI.getCurrent(),
                        e.getUsername(),
                        e.getPassword(),
                        () -> loginForm.setError(true)
                )
        );

        // Przycisk do logowania przez Google
        Button googleButton = new Button("Zaloguj przez Google", e ->
                UI.getCurrent()
                        .getPage()
                        .setLocation("http://localhost:8080/oauth2/authorization/google")
        );

        // Layout
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        add(loginForm, googleButton);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // 1) wyciągamy token z ?token=… (jeśli jest), zapisujemy w sesji i czyścimy URL
        // 2) jeśli w sesji jest już token → fetch + navigate
        auth.initLogin(attachEvent.getUI());
    }
}