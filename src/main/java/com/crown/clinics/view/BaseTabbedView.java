package com.crown.clinics.view;

import com.crown.clinics.component.WeatherWidget;
import com.crown.clinics.service.AuthService;
import com.crown.clinics.service.WeatherRestService;
import com.vaadin.flow.component.AttachEvent;
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

@SuppressWarnings("serial")
public abstract class BaseTabbedView extends VerticalLayout {

    protected final Tabs mainTabs;
    protected final VerticalLayout contentArea;
    protected final AuthService authService;
    private final WeatherWidget weatherWidget;

    private final Span userName = new Span("Ładowanie...");
    private final HorizontalLayout weatherLayout = new HorizontalLayout();

    public BaseTabbedView(String pageTitle, AuthService authService, WeatherRestService weatherRestService) {
        this.authService = authService;
        this.weatherWidget = new WeatherWidget(weatherRestService);

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        addClassName("base-tabbed-view");

        // Nagłówek
        H1 header = new H1(pageTitle);
        header.getStyle()
                .set("font-family", "'Courier New', monospace")
                .set("color", "#3e3e3e");

        // Pasek użytkownika z logout i pogodą
        Button logoutButton = new Button("Wyloguj", e -> authService.logout(UI.getCurrent()));
        HorizontalLayout userBar = new HorizontalLayout(weatherWidget, userName, logoutButton);
        userBar.setSpacing(true);
        userBar.setAlignItems(Alignment.CENTER);

        // Pasek nagłówkowy
        HorizontalLayout headerLayout = new HorizontalLayout(header, new Div(), userBar);
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.expand(header);

        // Zakładki
        Tab calendarTab  = new Tab("Kalendarz");
        Tab messagesTab = new Tab("Wiadomości");
        Tab boardsTab   = new Tab("Tablice");
        Tab patientsTab = new Tab("Pacjenci");

        mainTabs = new Tabs(calendarTab, messagesTab, boardsTab, patientsTab);
        mainTabs.setSelectedTab(calendarTab);
        mainTabs.addSelectedChangeListener(e -> updateContent());

        contentArea = new VerticalLayout();
        contentArea.setSizeFull();

        add(headerLayout, mainTabs, contentArea);
    }

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        UI ui = event.getUI();
        authService.fetchCurrentUser().subscribe(
                user -> ui.access(() -> {
                    userName.setText(user.firstName() + " " + user.lastName());
                }),
                err -> ui.access(() -> {
                    userName.setText("Nieznany użytkownik");
                    System.err.println("Błąd pobierania użytkownika:");
                    err.printStackTrace();
                })
        );
    }

    public abstract void beforeEnter(BeforeEnterEvent event);

    protected abstract void updateContent();

    protected Span defaultTabContent(String text) {
        return new Span(text + " - w przygotowaniu");
    }
}