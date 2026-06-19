package com.assistant.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AssistantServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssistantServerApplication.class, args);
	}

}
