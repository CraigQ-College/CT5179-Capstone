package com.vfc.vfc_backend;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VfcBackendApplication {

	@PostConstruct
	public void initialise() {}

	public static void main(String[] args) {
		SpringApplication.run(VfcBackendApplication.class, args);
	}

}
