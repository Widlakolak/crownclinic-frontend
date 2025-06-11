package com.crown.clinics.view;

import com.crown.clinics.component.CalendarComponent;
import com.crown.clinics.component.WeatherWidget;
import com.crown.clinics.service.AuthService;
import com.crown.clinics.service.WeatherRestService;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("doctor")
@PageTitle("CrownClinic - Lekarz")
@CssImport("./styles/doctor-styles.css")
public class DoctorView extends BaseTabbedView {

    private final CalendarComponent calendarComponent = new CalendarComponent();

    private final AuthService authService;
    private final WeatherWidget weatherWidget;

    public DoctorView(AuthService authService, WeatherRestService weatherRestService) {
        super("CrownClinic - Kartoteka", authService, weatherRestService);
        this.authService = authService;
        this.weatherWidget = new WeatherWidget(weatherRestService);
        updateContent();
    }

    @Override
    protected void updateContent() {
        contentArea.removeAll();
        String tab = mainTabs.getSelectedTab().getLabel();

        switch (tab) {
            case "Kalendarz" -> contentArea.add(calendarComponent);
            case "Wiadomości" -> contentArea.add(defaultTabContent("Wiadomości"));
            case "Tablice" -> contentArea.add(defaultTabContent("Tablice"));
            case "Pacjenci" -> contentArea.add(defaultTabContent("Pacjenci"));
            default -> contentArea.add(new Span("Nieznana zakładka"));
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authService.getToken() == null) {
            event.forwardTo("login");
        }
    }
}