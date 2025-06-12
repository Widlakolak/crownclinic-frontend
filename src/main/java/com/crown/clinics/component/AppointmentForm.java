package com.crown.clinics.component;

import com.crown.clinics.dto.AppointmentResponseDto;
import com.crown.clinics.dto.PatientResponseDto;
import com.crown.clinics.dto.UserResponseDto;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.util.List;

public class AppointmentForm extends FormLayout {

    public final DateTimePicker startDateTime = new DateTimePicker("Początek wizyty");
    public final DateTimePicker endDateTime = new DateTimePicker("Koniec wizyty");
    public final ComboBox<PatientResponseDto> patient = new ComboBox<>("Pacjent");    public final ComboBox<UserResponseDto> doctor = new ComboBox<>("Lekarz");
    public final ComboBox<String> status = new ComboBox<>("Status");
    public final TextArea notes = new TextArea("Notatki");

    private final Button save = new Button("Zapisz");
    private final Button delete = new Button("Usuń");
    private final Button close = new Button("Anuluj");
    private final Button addNewPatientButton = new Button(VaadinIcon.PLUS.create());

    private AppointmentResponseDto currentAppointment;

    public AppointmentForm(List<PatientResponseDto> patients, List<UserResponseDto> doctors) {
        addClassName("appointment-form");

        patient.setItems(patients);
        patient.setItemLabelGenerator(PatientResponseDto::getFullName);

        HorizontalLayout patientLayout = new HorizontalLayout(patient, addNewPatientButton);
        patientLayout.setAlignItems(Alignment.BASELINE);
        patient.getStyle().set("flex-grow", "1");

        addNewPatientButton.addClickListener(click -> {
            fireEvent(new AddNewPatientEvent(this));
        });

        doctor.setItems(doctors);
        doctor.setItemLabelGenerator(d -> d.firstName() + " " + d.lastName());

        status.setItems("SCHEDULED", "CANCELLED", "COMPLETED");

        add(startDateTime, endDateTime, patientLayout, doctor, status, notes, createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> fireEvent(new SaveEvent(this, currentAppointment)));
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, currentAppointment)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, close);
    }

    public void setPatientItems(List<PatientResponseDto> patients) {
        patient.setItems(patients);
    }

    public void setAppointment(AppointmentResponseDto appointment) {
        this.currentAppointment = appointment;
        if (appointment != null) {
            startDateTime.setValue(appointment.startDateTime() != null ? appointment.startDateTime().toLocalDateTime() : null);
            endDateTime.setValue(appointment.endDateTime() != null ? appointment.endDateTime().toLocalDateTime() : null);
            notes.setValue(appointment.notes() != null ? appointment.notes() : "");
            status.setValue(appointment.status());

            patient.getListDataView().getItems()
                    .filter(p -> p.getId().equals(appointment.patientId()))
                    .findFirst().ifPresent(patient::setValue);

            doctor.getListDataView().getItems()
                    .filter(d -> d.id().equals(appointment.doctorId()))
                    .findFirst().ifPresent(doctor::setValue);

            delete.setVisible(appointment.id() != null);
        }
    }

    public boolean isFormValid() {
        return !startDateTime.isEmpty() &&
                !endDateTime.isEmpty() &&
                !patient.isEmpty() &&
                !doctor.isEmpty();
    }

    @Getter
    public static abstract class AppointmentFormEvent extends ComponentEvent<AppointmentForm> {
        private final AppointmentResponseDto appointment;

        protected AppointmentFormEvent(AppointmentForm source, AppointmentResponseDto appointment) {
            super(source, false);
            this.appointment = appointment;
        }

    }

    public static class AddNewPatientEvent extends ComponentEvent<AppointmentForm> {
        AddNewPatientEvent(AppointmentForm source) { super(source, false); }
    }

    public static class SaveEvent extends AppointmentFormEvent {
        SaveEvent(AppointmentForm source, AppointmentResponseDto a) { super(source, a); }
    }

    public static class DeleteEvent extends AppointmentFormEvent {
        DeleteEvent(AppointmentForm source, AppointmentResponseDto a) { super(source, a); }
    }

    public static class CloseEvent extends AppointmentFormEvent {
        CloseEvent(AppointmentForm source) { super(source, null); }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}