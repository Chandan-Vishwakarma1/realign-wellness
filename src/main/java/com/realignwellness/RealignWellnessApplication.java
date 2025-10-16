package com.realignwellness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.realignwellness.repository")
public class RealignWellnessApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealignWellnessApplication.class, args);
	}

}
