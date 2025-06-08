package com.crown.clinics.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import elemental.json.Json;
import elemental.json.JsonObject;
import org.springframework.web.reactive.function.client.WebClient;

public class AvailabilityEditor extends Dialog {

    private final ComboBox<JsonObject> doctorBox = new ComboBox<>("Lekarz");
    private final DateTimePicker start = new DateTimePicker("Od");
    private final DateTimePicker end = new DateTimePicker("Do");
    private final Button saveButton = new Button("Zapisz");
    private final WebClient client = WebClient.create("http://localhost:8080/api");

    public AvailabilityEditor() {
        VerticalLayout layout = new VerticalLayout(doctorBox, start, end, saveButton);
        layout.setPadding(true);
        layout.setSpacing(true);

        doctorBox.setItemLabelGenerator(doc -> doc.getString("firstName") + " " + doc.getString("lastName"));
        loadDoctors();
        saveButton.addClickListener(e -> saveAvailability());

        add(layout);
    }

    private void loadDoctors() {
        client.get().uri("/users")
                .retrieve()
                .bodyToFlux(JsonObject.class)
                .filter(user -> "DOCTOR".equals(user.getString("role")))
                .collectList()
                .subscribe(users ->
                        getUI().ifPresent(ui -> ui.access(() -> doctorBox.setItems(users)))
                );
    }

    private void saveAvailability() {
        if (doctorBox.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Notification.show("Uzupełnij wszystkie pola", 3000, Notification.Position.MIDDLE);
            return;
        }

        JsonObject json = Json.createObject();
        json.put("doctorId", doctorBox.getValue().getNumber("id"));
        json.put("startDateTime", start.getValue().toString());
        json.put("endDateTime", end.getValue().toString());

        client.post().uri("/availability")
                .bodyValue(json)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        res -> Notification.show("Zapisano dostępność"),
                        err -> Notification.show("Błąd zapisu", 3000, Notification.Position.MIDDLE)
                );

        close();
    }
}
