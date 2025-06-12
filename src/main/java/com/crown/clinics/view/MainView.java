package com.crown.clinics.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "")
@PageTitle("Welcome")
public class MainView extends VerticalLayout {

    public MainView() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        add(new H1("Witaj w systemie Crown Clinic!"));

        Button loginButton = new Button("Zaloguj się");

        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);

        loginButton.addClickListener(click -> {
            UI.getCurrent().navigate(LoginView.class);
        });

        add(loginButton);
    }
}