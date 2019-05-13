package org.devocative.thallo.cdc.test;

import org.devocative.thallo.cdc.message.CdcEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class CdcTestApp {

	static int noOfEvent = 0;

	public static void main(String[] args) {
		SpringApplication.run(CdcTestApp.class, args);
	}

	@EventListener
	public void handleCdcEvent(CdcEvent event) {
		noOfEvent++;
	}
}
