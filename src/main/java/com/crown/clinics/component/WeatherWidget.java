package com.crown.clinics.component;

import com.crown.clinics.dto.WeatherDto;
import com.crown.clinics.service.WeatherRestService;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class WeatherWidget extends HorizontalLayout {

    private final Span temperatureSpan = new Span();
    private final Image iconImage = new Image();

    public WeatherWidget(WeatherRestService weatherRestService) {
        initLayout();
        loadWeather(weatherRestService, "Warsaw");
    }

    private void initLayout() {
        setAlignItems(Alignment.CENTER);
        temperatureSpan.getStyle().set("font-weight", "bold").set("margin-right", "8px");
        iconImage.setWidth("32px");
        iconImage.setHeight("32px");
        add(temperatureSpan, iconImage);
    }

    private void loadWeather(WeatherRestService weatherRestService, String city) {
        weatherRestService.getTodayWeather(city).subscribe(
                weatherDto -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        updateWeatherUI(weatherDto);
                    }));
                },
                error -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        System.err.println("Nie udało się załadować pogody, ukrywam widget.");
                        setVisible(false);
                    }));
                }
        );
    }

    private void updateWeatherUI(WeatherDto weather) {
        temperatureSpan.setText(Math.round(weather.temperature()) + "°C");
        iconImage.setSrc("https://openweathermap.org/img/wn/" + weather.icon() + "@2x.png");
        iconImage.setAlt(weather.description());
    }
}