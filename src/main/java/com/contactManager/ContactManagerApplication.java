package com.contactManager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ContactManagerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ContactManagerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
