package com.efeerturk.smart_ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.efeerturk.smart_ticket"})
@EntityScan(basePackages = {"com.efeerturk.smart_ticket"})
@ComponentScan(basePackages = {"com.efeerturk.smart_ticket"})
@SpringBootApplication
public class SmartTicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartTicketApplication.class, args);
	}

}
