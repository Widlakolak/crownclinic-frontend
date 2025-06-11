package com.crown.clinics.view;

import com.crown.clinics.component.CalendarComponent;
import com.crown.clinics.component.WeatherWidget;
import com.crown.clinics.service.AuthService;
import com.crown.clinics.service.WeatherRestService;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("reception")
@PageTitle("CrownClinic - Recepcja")
public class ReceptionView extends BaseTabbedView {

    private final AuthService authService;
    private final WeatherWidget weatherWidget;

    public ReceptionView(AuthService authService, WeatherRestService weatherRestService) {
        super("CrownClinic - Recepcja", authService, weatherRestService);
        this.authService = authService;
        this.weatherWidget = new WeatherWidget(weatherRestService);
        updateContent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authService.getToken() == null) {
            event.forwardTo("login");
        }
    }

    @Override
    protected void updateContent() {
        contentArea.removeAll();

        String selected = mainTabs.getSelectedTab().getLabel();
        switch (selected) {
            case "Kalendarz" -> {
                CalendarComponent calendar = new CalendarComponent();
                contentArea.add(calendar);
            }
            case "Wiadomości" -> contentArea.add(defaultTabContent("Wiadomości"));
            case "Tablice" -> contentArea.add(defaultTabContent("Tablice"));
            case "Pacjenci" -> {
//                AvailabilityEditor editor = new AvailabilityEditor();
//                VerticalLayout layout = new VerticalLayout(editor);
//                layout.setPadding(false);
//                layout.setSpacing(false);
//                contentArea.add(layout);
            }
            default -> contentArea.add(new Span("Nieznana zakładka"));
        }
    }
}
