package com.crown.clinics.view;

import com.crown.clinics.component.AppointmentForm;
import com.crown.clinics.component.CalendarComponent;
import com.crown.clinics.component.PatientForm;
import com.crown.clinics.dto.AppointmentRequestDto;
import com.crown.clinics.dto.AppointmentResponseDto;
import com.crown.clinics.dto.PatientRequestDto;
import com.crown.clinics.dto.PatientResponseDto;
import com.crown.clinics.service.AuthService;
import com.crown.clinics.service.BackendService;
import com.crown.clinics.service.WeatherRestService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Route("doctor")
@PageTitle("CrownClinic - Lekarz")
public class DoctorView extends BaseTabbedView {

    private CalendarComponent calendarComponent;
    private AppointmentForm appointmentForm;

    private final BackendService backendService;

    public DoctorView(AuthService authService, WeatherRestService weatherRestService, BackendService backendService) {
        super("Panel Lekarza", authService, weatherRestService);
        this.backendService = backendService;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (contentArea.getComponentCount() == 0) {
            updateContent();
        }
    }

    @Override
    protected void updateContent() {
        contentArea.removeAll();
        String selectedTab = mainTabs.getSelectedTab().getLabel();

        if ("Kalendarz".equals(selectedTab)) {
            setupCalendarLayout();
        } else {
            contentArea.add(defaultTabContent(selectedTab));
        }
    }

    private void setupCalendarLayout() {
        calendarComponent = new CalendarComponent();
        appointmentForm = new AppointmentForm(List.of(), List.of());

        appointmentForm.setWidth("32em");
        appointmentForm.setVisible(false);
        calendarComponent.setDoctorFilterVisible(false);

        HorizontalLayout mainLayout = new HorizontalLayout(calendarComponent, appointmentForm);
        mainLayout.setSizeFull();
        mainLayout.setFlexGrow(2, calendarComponent);
        mainLayout.setFlexGrow(1, appointmentForm);

        Button addAppointmentButton = new Button("Dodaj wizytę", click -> addAppointment());

        contentArea.add(addAppointmentButton, mainLayout);
        setupListeners();

        Mono.when(refreshCalendar(), loadFormSelects()).subscribe();
    }

    private void setupListeners() {
        calendarComponent.addListener(CalendarComponent.AppointmentSelectEvent.class, e -> editAppointment(e.getAppointment()));
        appointmentForm.addListener(AppointmentForm.SaveEvent.class, this::handleSaveEvent);
        appointmentForm.addListener(AppointmentForm.DeleteEvent.class, this::handleDeleteEvent);
        appointmentForm.addListener(AppointmentForm.CloseEvent.class, e -> closeEditor());
        appointmentForm.addListener(AppointmentForm.AddNewPatientEvent.class, e -> openNewPatientDialog());
    }

    private Mono<Void> refreshCalendar() {
        return backendService.getAppointments()
                .doOnSuccess(appointments ->
                        getUI().ifPresent(ui -> ui.access(() ->
                                calendarComponent.setAppointments(appointments)
                        ))
                )
                .then();
    }

    private Mono<Void> loadFormSelects() {
        Mono<Void> loadPatients = backendService.getPatients()
                .doOnSuccess(patients ->
                        getUI().ifPresent(ui -> ui.access(() ->
                                appointmentForm.patient.setItems(patients)
                        ))
                ).then();

        Mono<Void> loadDoctors = backendService.getDoctors()
                .doOnSuccess(doctors ->
                        getUI().ifPresent(ui -> ui.access(() ->
                                appointmentForm.doctor.setItems(doctors)
                        ))
                ).then();

        return Mono.when(loadPatients, loadDoctors);
    }

    private void handleSaveEvent(AppointmentForm.SaveEvent event) {
        if (!appointmentForm.isFormValid()) {
            Notification.show("Proszę wypełnić wszystkie wymagane pola.");
            return;
        }

        AppointmentRequestDto requestDto = new AppointmentRequestDto(
                ZonedDateTime.of(appointmentForm.startDateTime.getValue(), ZoneId.systemDefault()),
                ZonedDateTime.of(appointmentForm.endDateTime.getValue(), ZoneId.systemDefault()),
                appointmentForm.notes.getValue(),
                appointmentForm.patient.getValue().getId(),
                appointmentForm.doctor.getValue().id(),
                appointmentForm.status.getValue()
        );

        Mono<AppointmentResponseDto> saveOperation;
        if (event.getAppointment().id() == null) {
            saveOperation = backendService.createAppointment(requestDto);
        } else {
            saveOperation = backendService.updateAppointment(event.getAppointment().id(), requestDto);
        }

        saveOperation
                .then(refreshCalendar())
                .subscribe(
                        null,
                        error -> getUI().ifPresent(ui -> ui.access(() ->
                                Notification.show("Błąd zapisu: " + error.getMessage())
                        )),
                        () -> getUI().ifPresent(ui -> ui.access(() -> {
                            Notification.show("Wizyta zapisana.");
                            closeEditor();
                        }))
                );
    }

    private void handleDeleteEvent(AppointmentForm.DeleteEvent event) {
        if (event.getAppointment() == null || event.getAppointment().id() == null) return;

        backendService.deleteAppointment(event.getAppointment().id())
                .then(refreshCalendar())
                .subscribe(
                        null,
                        error -> getUI().ifPresent(ui -> ui.access(() ->
                                Notification.show("Błąd usuwania: " + error.getMessage()))),
                        () -> getUI().ifPresent(ui -> ui.access(() -> {
                            Notification.show("Wizyta usunięta.");
                            closeEditor();
                        }))
                );
    }

    private void openNewPatientDialog() {
        Dialog dialog = new Dialog();
        PatientForm newPatientForm = new PatientForm();
        newPatientForm.setPatient(new PatientResponseDto());

        newPatientForm.addListener(PatientForm.SaveEvent.class, event -> {
            PatientResponseDto formData = event.getPatient();
            PatientRequestDto newPatientDto = new PatientRequestDto(
                    formData.getFirstName(), formData.getLastName(),
                    formData.getEmail(), formData.getPhone(), formData.getDateOfBirth()
            );

            backendService.createPatient(newPatientDto)
                    .flatMap(createdPatient -> {
                        return backendService.getPatients();
                    })
                    .subscribe(
                            allPatients -> getUI().ifPresent(ui -> ui.access(() -> {
                                appointmentForm.patient.setItems(allPatients);
                                Notification.show("Pacjent dodany pomyślnie.");
                                dialog.close();
                            })),
                            error -> getUI().ifPresent(ui -> ui.access(() ->
                                    Notification.show("Błąd zapisu pacjenta: " + error.getMessage())))
                    );
        });

        newPatientForm.addListener(PatientForm.CloseEvent.class, e -> dialog.close());
        dialog.add(newPatientForm);
        dialog.open();
    }

    private void addAppointment() {
        calendarComponent.getAppointmentGrid().asSingleSelect().clear();
        editAppointment(new AppointmentResponseDto(null, null, null, "", "SCHEDULED", null, null, null, null, null, null, null));
    }

    public void editAppointment(AppointmentResponseDto appointment) {
        if (appointment == null) {
            closeEditor();
        } else {
            appointmentForm.setAppointment(appointment);
            appointmentForm.setVisible(true);
        }
    }

    private void closeEditor() {
        appointmentForm.setAppointment(null);
        appointmentForm.setVisible(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authService.getToken() == null) {
            event.forwardTo("login");
        }
    }
}