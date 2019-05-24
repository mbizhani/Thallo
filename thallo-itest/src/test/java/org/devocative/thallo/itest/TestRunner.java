package org.devocative.thallo.itest;

public class TestRunner {

	public static void main(String[] args) {
		ITestFactory factory = new ITestFactory("classpath:/itest.xml");
		try {
			factory.init();
			factory.start();
		} finally {
			factory.stop();
		}
	}
}
