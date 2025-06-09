package com.crown.clinics;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@Push
@PWA(name = "Clinics App", shortName = "Clinics")
@AnonymousAllowed
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ClinicsApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(ClinicsApplication.class, args);
	}
}