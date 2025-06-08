package com.crown.clinics.view;

import com.crown.clinics.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;

public abstract class BaseTabbedView extends VerticalLayout {

    protected final Tabs mainTabs;
    protected final VerticalLayout contentArea;
    protected final AuthService authService;

    public BaseTabbedView(String pageTitle, AuthService authService) {
        this.authService = authService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        addClassName("base-tabbed-view");

        // Nagłówek
        H1 header = new H1(pageTitle);
        header.getStyle()
                .set("font-family", "'Courier New', monospace")
                .set("color", "#3e3e3e");

        // Pasek użytkownika i logout
        Span userName = new Span("Użytkownik");
        Button logoutButton = new Button("Wyloguj", e -> {
            authService.logout();
        });
        HorizontalLayout userBar = new HorizontalLayout(userName, logoutButton);
        userBar.setSpacing(true);
        userBar.setAlignItems(Alignment.CENTER);

        // Asynchroniczne pobranie danych użytkownika
        authService.fetchCurrentUser().subscribe(user -> {
            UI.getCurrent().access(() -> {
                userName.setText(user.firstName() + " " + user.lastName());
            });
        });

        // Pasek nagłówkowy
        HorizontalLayout headerLayout = new HorizontalLayout(header, new Div(), userBar);
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.expand(header); // wypycha header na lewo, userBar na prawo

        // Zakładki
        Tab calendarTab = new Tab("Kalendarz");
        Tab messagesTab = new Tab("Wiadomości");
        Tab boardsTab = new Tab("Tablice");
        Tab patientsTab = new Tab("Pacjenci");

        mainTabs = new Tabs(calendarTab, messagesTab, boardsTab, patientsTab);
        mainTabs.setSelectedTab(calendarTab);
        mainTabs.addSelectedChangeListener(event -> updateContent());

        contentArea = new VerticalLayout();
        contentArea.setSizeFull();

        add(headerLayout, mainTabs, contentArea);
    }

    public abstract void beforeEnter(BeforeEnterEvent event);

    protected abstract void updateContent();

    protected Span defaultTabContent(String text) {
        return new Span(text + " - w przygotowaniu");
    }
}
