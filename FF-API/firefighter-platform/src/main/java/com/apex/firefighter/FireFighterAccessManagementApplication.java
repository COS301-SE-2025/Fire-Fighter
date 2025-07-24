package com.apex.firefighter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FireFighterAccessManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FireFighterAccessManagementApplication.class, args);
	}

}
