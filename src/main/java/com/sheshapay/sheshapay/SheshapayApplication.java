package com.sheshapay.sheshapay;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class SheshapayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SheshapayApplication.class, args);
	}

    @PostConstruct
    public void init() {
        // Set JVM default timezone to South Africa
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Johannesburg"));
    }
}
