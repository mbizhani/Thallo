package org.devocative.thallo.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"x.y.z", "org.devocative.thallo.core"})
public class CoreTestApp {

	public static void main(String[] args) {
		SpringApplication.run(CoreTestApp.class, args);
	}

}
