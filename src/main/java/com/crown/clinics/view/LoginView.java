package com.crown.clinics.view;

import com.crown.clinics.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.PageTitle;
import org.springframework.beans.factory.annotation.Autowired;

@Route("login")
@PageTitle("Logowanie")
@CssImport("./styles/styles.css")
public class LoginView extends VerticalLayout {

    private final AuthService authService;
    private final LoginForm loginForm = new LoginForm();

    @Autowired
    public LoginView(AuthService authService) {
        this.authService = authService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("login-view");

        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.addLoginListener(event -> {
            String u = event.getUsername();
            String p = event.getPassword();
            UI ui = UI.getCurrent();
            authService.login(u, p)
                    .subscribe(token -> {
                        ui.access(() -> authService.handleTokenFromUrl(token, ui));
                    }, error -> {
                        ui.access(() -> loginForm.setError(true));
                    });
        });

        Button googleLoginButton = new Button("Zaloguj przez Google", e -> {
            UI.getCurrent().getPage().setLocation("http://localhost:8080/oauth2/authorization/google");
        });

        add(loginForm, googleLoginButton);
    }

    public void beforeEnter(BeforeEnterEvent event) {
        UI uiBefore = event.getUI();
        authService.handleLoginViewAttach(uiBefore);
    }
}
