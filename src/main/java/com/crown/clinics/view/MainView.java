package com.crown.clinics.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Dashboard")
public class MainView extends VerticalLayout {
    public MainView() {
        add(new H1("Witaj w aplikacji CrownClinic!"));
    }
}
