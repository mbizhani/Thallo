package org.devocative.thallo.itest;

public class TestRunner {

	public static void main(String[] args) {
		ITestFactory factory = new ITestFactory();
		factory.run();
	}
}
