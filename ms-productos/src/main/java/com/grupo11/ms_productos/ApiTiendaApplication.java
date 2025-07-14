package com.grupo11.ms_productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ApiTiendaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTiendaApplication.class, args);
	}

}
