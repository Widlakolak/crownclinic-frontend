package com.crown.clinics.component;

import com.crown.clinics.dto.PatientResponseDto;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

public class PatientForm extends FormLayout {

    TextField firstName = new TextField("Imię");
    TextField lastName = new TextField("Nazwisko");
    EmailField email = new EmailField("Email");
    TextField phone = new TextField("Telefon");
    DatePicker dateOfBirth = new DatePicker("Data urodzenia");

    Button save = new Button("Zapisz");
    Button close = new Button("Anuluj");

    Binder<PatientResponseDto> binder = new BeanValidationBinder<>(PatientResponseDto.class);

    public PatientForm() {
        addClassName("patient-form");
        binder.bindInstanceFields(this);

        add(firstName, lastName, email, phone, dateOfBirth, createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, close);
    }

    public void setPatient(PatientResponseDto patient) {
        binder.setBean(patient);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    @Getter
    public static abstract class PatientFormEvent extends ComponentEvent<PatientForm> {
        private final PatientResponseDto patient;
        protected PatientFormEvent(PatientForm source, PatientResponseDto patient) {
            super(source, false);
            this.patient = patient;
        }
    }
    public static class SaveEvent extends PatientFormEvent {
        SaveEvent(PatientForm source, PatientResponseDto p) { super(source, p); }
    }
    public static class CloseEvent extends PatientFormEvent {
        CloseEvent(PatientForm source) { super(source, null); }
    }
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}