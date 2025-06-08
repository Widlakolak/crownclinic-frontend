package com.crown.clinics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ClinicsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicsApplication.class, args);
	}

}
