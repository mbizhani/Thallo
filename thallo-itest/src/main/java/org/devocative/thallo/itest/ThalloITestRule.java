package org.devocative.thallo.itest;

import org.junit.rules.ExternalResource;

public class ThalloITestRule extends ExternalResource {
	private final ITestFactory testFactory;

	// ------------------------------

	public ThalloITestRule(String xmlFile) {
		testFactory = new ITestFactory(xmlFile);
	}

	// ------------------------------

	@Override
	protected void before() {
		testFactory.init();
	}

	@Override
	protected void after() {
		testFactory.stop();
	}

	// ---------------

	public void run() {
		testFactory.start();
	}
}
