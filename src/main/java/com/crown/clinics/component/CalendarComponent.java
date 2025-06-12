package com.crown.clinics.component;

import com.crown.clinics.dto.AppointmentResponseDto;
import com.crown.clinics.dto.UserResponseDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarComponent extends VerticalLayout {

    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final Grid<AppointmentResponseDto> appointmentGrid = new Grid<>(AppointmentResponseDto.class);
    private final ComboBox<UserResponseDto> doctorFilter = new ComboBox<>("Filtruj lekarza");

    private List<AppointmentResponseDto> allAppointments;

    public CalendarComponent() {
        addClassName("calendar-component");

        // Toolbar
        Button prevDay = new Button("←", e -> datePicker.setValue(datePicker.getValue().minusDays(1)));
        Button nextDay = new Button("→", e -> datePicker.setValue(datePicker.getValue().plusDays(1)));
        datePicker.addValueChangeListener(e -> fireEvent(new DateChangeEvent(this, e.getValue())));

        HorizontalLayout dateNav = new HorizontalLayout(prevDay, datePicker, nextDay);
        dateNav.setAlignItems(Alignment.CENTER);

        doctorFilter.setItemLabelGenerator(d -> d.firstName() + " " + d.lastName());
        doctorFilter.setClearButtonVisible(true);
        doctorFilter.addValueChangeListener(e -> filterGrid()); // Filtrujemy przy zmianie lekarza

        HorizontalLayout toolbar = new HorizontalLayout(dateNav, doctorFilter);
        toolbar.setSpacing(true);

        configureGrid();
        add(toolbar, appointmentGrid);
    }

    private void configureGrid() {
        appointmentGrid.setSizeFull();
        appointmentGrid.setColumns();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        appointmentGrid.addColumn(app -> app.startDateTime().format(timeFormatter)).setHeader("Godzina").setSortable(true);
        appointmentGrid.addColumn(AppointmentResponseDto::patientFullName).setHeader("Pacjent").setSortable(true);
        appointmentGrid.addColumn(AppointmentResponseDto::doctorFullName).setHeader("Lekarz").setSortable(true);
        appointmentGrid.addColumn(AppointmentResponseDto::status).setHeader("Status");
        appointmentGrid.asSingleSelect().addValueChangeListener(event ->
                fireEvent(new AppointmentSelectEvent(this, event.getValue())));
    }

    // Metoda do przekazywania danych z zewnątrz
    public void setAppointments(List<AppointmentResponseDto> appointments) {
        this.allAppointments = appointments;
        filterGrid();
    }

    // Metoda do przekazywania listy lekarzy do filtra
    public void setDoctorsForFilter(List<UserResponseDto> doctors) {
        doctorFilter.setItems(doctors);
    }

    // Metoda ukrywająca filtr (przydatne w DoctorView)
    public void setDoctorFilterVisible(boolean visible) {
        doctorFilter.setVisible(visible);
    }

    private void filterGrid() {
        if (allAppointments == null) return;

        UserResponseDto selectedDoctor = doctorFilter.getValue();
        LocalDate selectedDate = datePicker.getValue();

        List<AppointmentResponseDto> filteredAppointments = allAppointments.stream()
                .filter(app -> app.startDateTime().toLocalDate().equals(selectedDate))
                .filter(app -> selectedDoctor == null || app.doctorId().equals(selectedDoctor.id()))
                .collect(Collectors.toList());

        appointmentGrid.setItems(filteredAppointments);
    }

    // Zdarzenia do komunikacji z widokiem nadrzędnym
    public static class DateChangeEvent extends ComponentEvent<CalendarComponent> {
        private final LocalDate date;
        public DateChangeEvent(CalendarComponent source, LocalDate date) {
            super(source, false);
            this.date = date;
        }
        public LocalDate getDate() { return date; }
    }

    public static class AppointmentSelectEvent extends ComponentEvent<CalendarComponent> {
        private final AppointmentResponseDto appointment;
        public AppointmentSelectEvent(CalendarComponent source, AppointmentResponseDto appointment) {
            super(source, false);
            this.appointment = appointment;
        }
        public AppointmentResponseDto getAppointment() { return appointment; }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}