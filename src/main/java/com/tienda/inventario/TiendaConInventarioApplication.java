package com.tienda.inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
 public class TiendaConInventarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaConInventarioApplication.class, args);
	}

}
