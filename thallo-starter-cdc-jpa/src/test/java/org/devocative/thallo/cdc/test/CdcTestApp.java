package org.devocative.thallo.cdc.test;

import org.devocative.thallo.cdc.event.CdcEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class CdcTestApp {

	static int noOfCreated = 0;
	static int noOfUpdated = 0;
	static int noOfDeleted = 0;

	public static void main(String[] args) {
		SpringApplication.run(CdcTestApp.class, args);
	}

	@EventListener
	public void handleCdcEvent(CdcEvent event) {
		switch (event.getType()) {

			case Created:
				noOfCreated++;
				break;

			case Updated:
				noOfUpdated++;
				break;

			case Deleted:
				noOfDeleted++;
				break;

			default:
				throw new RuntimeException("Invalid literal!");
		}
	}
}
