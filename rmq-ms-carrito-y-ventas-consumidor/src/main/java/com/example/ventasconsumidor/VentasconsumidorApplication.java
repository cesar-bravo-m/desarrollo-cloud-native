package com.example.ventasconsumidor;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class VentasconsumidorApplication {

	public static void main(String[] args) {
		SpringApplication.run(VentasconsumidorApplication.class, args);
	}

}
