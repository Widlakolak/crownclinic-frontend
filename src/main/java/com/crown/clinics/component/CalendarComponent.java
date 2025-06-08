package com.crown.clinics.component;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import elemental.json.JsonObject;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

public class CalendarComponent extends VerticalLayout {

    private final DatePicker datePicker;
    private final Button prevButton;
    private final Button nextButton;
    private final Button dayViewButton;
    private final Button weekViewButton;
    private final Button monthViewButton;
    private final Div calendarView;
    private final WebClient client = WebClient.create("http://localhost:8080/api");

    private LocalDate currentDate;
    private String currentView;

    public CalendarComponent() {
        setSizeFull();
        addClassName("calendar-component");

        currentDate = LocalDate.now();
        currentView = "Day";

        // Navigation
        prevButton = new Button("←", e -> changeDate(currentDate.minusDays(1)));
        nextButton = new Button("→", e -> changeDate(currentDate.plusDays(1)));
        datePicker = new DatePicker(currentDate);
        datePicker.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                changeDate(event.getValue());
            }
        });

        // View buttons
        dayViewButton = new Button("Dzień", e -> changeView("Day"));
        weekViewButton = new Button("Tydzień", e -> changeView("Week"));
        monthViewButton = new Button("Miesiąc", e -> changeView("Month"));

        HorizontalLayout navLayout = new HorizontalLayout(prevButton, datePicker, nextButton);
        HorizontalLayout viewLayout = new HorizontalLayout(dayViewButton, weekViewButton, monthViewButton);

        calendarView = new Div();
        calendarView.setSizeFull();
        calendarView.addClassName("calendar-view");

        add(navLayout, viewLayout, calendarView);
        updateCalendarView();
        updateActiveViewButton();
    }

    private void changeDate(LocalDate newDate) {
        this.currentDate = newDate;
        datePicker.setValue(newDate);
        updateCalendarView();
    }

    private void changeView(String view) {
        this.currentView = view;
        updateCalendarView();
        updateActiveViewButton();
    }

    private void updateCalendarView() {
        calendarView.removeAll();
        calendarView.add(new Div(new Text("Widok: " + currentView + ", Data: " + currentDate)));

        client.get()
                .uri("/appointments")
                .retrieve()
                .bodyToFlux(JsonObject.class)
                .collectList()
                .subscribe(this::renderAppointments, error -> Notification.show("Błąd ładowania wizyt"));
    }

    private void updateActiveViewButton() {
        dayViewButton.removeClassName("active");
        weekViewButton.removeClassName("active");
        monthViewButton.removeClassName("active");

        switch (currentView) {
            case "Day" -> dayViewButton.addClassName("active");
            case "Week" -> weekViewButton.addClassName("active");
            case "Month" -> monthViewButton.addClassName("active");
        }
    }

    private void renderAppointments(List<JsonObject> appointments) {
        getUI().ifPresent(ui -> ui.access(() -> {
            for (JsonObject obj : appointments) {
                String start = obj.getString("startDateTime");
                String doctor = obj.getString("doctorFullName");
                String patient = obj.getString("patientFullName");
                calendarView.add(new Div(new Text(start + ": " + doctor + " — " + patient)));
            }
        }));
    }
}
