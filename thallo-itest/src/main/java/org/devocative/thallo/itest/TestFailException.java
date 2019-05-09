package org.devocative.thallo.itest;

public class TestFailException extends RuntimeException {

	public TestFailException(String message, Object expected, Object result) {
		this(String.format("%s: [%s] expected, but [%s]", message, expected, result));
	}

	public TestFailException(String message) {
		super(message);
	}
}
