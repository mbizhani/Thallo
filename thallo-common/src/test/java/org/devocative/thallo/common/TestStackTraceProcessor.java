package org.devocative.thallo.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import x.y.z.DummyExceptionGen;

public class TestStackTraceProcessor {

	@Test
	public void testStackTraceProcessor_withWarp() {
		try {
			method1(true);
			Assertions.fail("Expecting Exception");
		} catch (Exception e) {
			final String filtered = StackTraceProcessor.filter(e, StackTraceProcessor.class);

			System.out.println("filtered_withWarp = " + filtered);

			Assertions.assertEquals(
				"org.devocative.thallo.common.Level2Exception: x.y.z.Level1Exception\n" +
					"\tat org.devocative.thallo.common.TestStackTraceProcessor.method3(TestStackTraceProcessor.java:74)\n" +
					"\tat org.devocative.thallo.common.TestStackTraceProcessor.method2(TestStackTraceProcessor.java:66)\n" +
					"\t...\n" +
					"Caused by: x.y.z.Level1Exception\n" +
					"\tat x.y.z.DummyExceptionGen.test5(DummyExceptionGen.java:25)\n" +
					"\t...\n" +
					"\tat x.y.z.DummyExceptionGen.excGen(DummyExceptionGen.java:5)\n" +
					"\tat org.devocative.thallo.common.TestStackTraceProcessor.method3(TestStackTraceProcessor.java:72)\n" +
					"\tat org.devocative.thallo.common.TestStackTraceProcessor.method2(TestStackTraceProcessor.java:66)\n" +
					"\tat org.devocative.thallo.common.TestStackTraceProcessor.method1(TestStackTraceProcessor.java:62)\n" +
					"\tat org.devocative.thallo.common.TestStackTraceProcessor.testStackTraceProcessor_withWarp(TestStackTraceProcessor.java:12)\n" +
					"\t...\n", filtered);
		}
	}

	@Test
	public void testStackTraceProcessor_withoutWarp() {
		try {
			method1(false);
			Assertions.fail("Expecting Exception");
		} catch (Exception e) {
			final String filtered = StackTraceProcessor.filter(e, StackTraceProcessor.class);

			System.out.println("filtered_withoutWarp = " + filtered);

			Assertions.assertEquals(
				"x.y.z.Level1Exception\n" +
					"\tat x.y.z.DummyExceptionGen.test5(DummyExceptionGen.java:25)\n" +
					"\t...\n" +
					"\tat x.y.z.DummyExceptionGen.excGen(DummyExceptionGen.java:5)\n" +
					"\tat org.devocative.thallo.common.TestStackTraceProcessor.method3(TestStackTraceProcessor.java:77)\n" +
					"\tat org.devocative.thallo.common.TestStackTraceProcessor.method2(TestStackTraceProcessor.java:66)\n" +
					"\tat org.devocative.thallo.common.TestStackTraceProcessor.method1(TestStackTraceProcessor.java:62)\n" +
					"\tat org.devocative.thallo.common.TestStackTraceProcessor.testStackTraceProcessor_withoutWarp(TestStackTraceProcessor.java:39)\n" +
					"\t...\n", filtered);
		}
	}

	// ------------------------------

	private void method1(boolean wrap) {
		method2(wrap);
	}

	private void method2(boolean wrap) {
		method3(wrap);
	}

	private void method3(boolean wrap) {
		if (wrap) {
			try {
				DummyExceptionGen.excGen();
			} catch (Exception e) {
				throw new Level2Exception(e);
			}
		} else {
			DummyExceptionGen.excGen();
		}
	}
}
