package com.fipematch.fipematch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import main.Principal;

@SpringBootApplication
public class FipematchApplication  implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FipematchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal fipe = new Principal();
		fipe.consultar();
	}

}
