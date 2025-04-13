package com.finanzas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.finanzas")
public class FinanzasApp {

	public static void main(String[] args) {
		SpringApplication.run(FinanzasApp.class, args);
	}

}
