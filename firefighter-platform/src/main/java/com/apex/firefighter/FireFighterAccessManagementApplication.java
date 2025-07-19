package com.apex.firefighter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FireFighterAccessManagementApplication {

	public static void main(String[] args) {
		System.out.println("\n" + "=".repeat(70));
		System.out.println("🚒 STARTING FIREFIGHTER PLATFORM");
		System.out.println("=".repeat(70));
		System.out.println("🔧 Loading configurations...");
		System.out.println("📧 Initializing email service...");
		System.out.println("🤖 Setting up AI chatbot...");
		System.out.println("📚 Preparing Swagger documentation...");
		System.out.println("=".repeat(70) + "\n");

		SpringApplication.run(FireFighterAccessManagementApplication.class, args);
	}

}
