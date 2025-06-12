package com.crown.clinics.view;

import com.crown.clinics.component.AppointmentForm;
import com.crown.clinics.component.CalendarComponent;
import com.crown.clinics.dto.AppointmentRequestDto;
import com.crown.clinics.dto.AppointmentResponseDto;
import com.crown.clinics.service.AuthService;
import com.crown.clinics.service.BackendService;
import com.crown.clinics.service.WeatherRestService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

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
        refreshCalendar();
    }

    private void setupListeners() {
        calendarComponent.addListener(CalendarComponent.AppointmentSelectEvent.class, e -> editAppointment(e.getAppointment()));
        appointmentForm.addListener(AppointmentForm.SaveEvent.class, this::saveAppointment);
        appointmentForm.addListener(AppointmentForm.DeleteEvent.class, this::deleteAppointment);
        appointmentForm.addListener(AppointmentForm.CloseEvent.class, e -> closeEditor());
    }

    private void refreshCalendar() {
        List<AppointmentResponseDto> appointments = backendService.getAppointments();
        calendarComponent.setAppointments(appointments);
    }

    private void saveAppointment(AppointmentForm.SaveEvent event) {
        if (!appointmentForm.isFormValid()) {
            Notification.show("Proszę wypełnić wszystkie wymagane pola.");
            return;
        }

        AppointmentRequestDto requestDto = new AppointmentRequestDto(
                ZonedDateTime.of(appointmentForm.startDateTime.getValue(), ZoneId.systemDefault()),
                ZonedDateTime.of(appointmentForm.endDateTime.getValue(), ZoneId.systemDefault()),
                appointmentForm.notes.getValue(),
                appointmentForm.patient.getValue().id(),
                appointmentForm.doctor.getValue().id(),
                appointmentForm.status.getValue()
        );

        if (event.getAppointment().id() == null) {
            backendService.createAppointment(requestDto);
        } else {
            backendService.updateAppointment(event.getAppointment().id(), requestDto);
        }

        refreshCalendar();
        closeEditor();
    }

    private void deleteAppointment(AppointmentForm.DeleteEvent event) {
        backendService.deleteAppointment(event.getAppointment().id());
        refreshCalendar();
        closeEditor();
    }

    private void addAppointment() {
        loadFormSelects();
        editAppointment(new AppointmentResponseDto(null, null, null, "", "SCHEDULED", null, null, null, null, null, null, null));
    }

    public void editAppointment(AppointmentResponseDto appointment) {
        if (appointment == null) {
            closeEditor();
        } else {
            loadFormSelects();
            appointmentForm.setAppointment(appointment);
            appointmentForm.setVisible(true);
        }
    }

    private void loadFormSelects() {
        appointmentForm.patient.setItems(backendService.getPatients());
        appointmentForm.doctor.setItems(backendService.getDoctors());
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