package org.devocative.thallo.test;

import org.devocative.thallo.common.StackTraceProcessor;
import org.junit.Assert;
import org.junit.Test;
import x.y.z.DummyExceptionGen;

public class TestStackTraceProcessor {

	@Test
	public void testStackTraceProcessor_withWarp() {
		try {
			method1(true);
			Assert.fail("Expecting Exception");
		} catch (Exception e) {
			final String filtered = StackTraceProcessor.filter(e, StackTraceProcessor.class);

			//System.out.println("filtered_withWarp = " + filtered);

			Assert.assertEquals(
				"org.devocative.thallo.test.Level2Exception: x.y.z.Level1Exception\n" +
					"\tat org.devocative.thallo.test.TestStackTraceProcessor.method3(TestStackTraceProcessor.java:75)\n" +
					"\tat org.devocative.thallo.test.TestStackTraceProcessor.method2(TestStackTraceProcessor.java:67)\n" +
					"\t...\n" +
					"Caused by: x.y.z.Level1Exception\n" +
					"\tat x.y.z.DummyExceptionGen.test5(DummyExceptionGen.java:25)\n" +
					"\t...\n" +
					"\tat x.y.z.DummyExceptionGen.excGen(DummyExceptionGen.java:5)\n" +
					"\tat org.devocative.thallo.test.TestStackTraceProcessor.method3(TestStackTraceProcessor.java:73)\n" +
					"\tat org.devocative.thallo.test.TestStackTraceProcessor.method2(TestStackTraceProcessor.java:67)\n" +
					"\tat org.devocative.thallo.test.TestStackTraceProcessor.method1(TestStackTraceProcessor.java:63)\n" +
					"\tat org.devocative.thallo.test.TestStackTraceProcessor.testStackTraceProcessor_withWarp(TestStackTraceProcessor.java:13)\n" +
					"\t...\n", filtered);
		}
	}

	@Test
	public void testStackTraceProcessor_withoutWarp() {
		try {
			method1(false);
			Assert.fail("Expecting Exception");
		} catch (Exception e) {
			final String filtered = StackTraceProcessor.filter(e, StackTraceProcessor.class);

			//System.out.println("filtered_withoutWarp = " + filtered);

			Assert.assertEquals(
				"x.y.z.Level1Exception\n" +
					"\tat x.y.z.DummyExceptionGen.test5(DummyExceptionGen.java:25)\n" +
					"\t...\n" +
					"\tat x.y.z.DummyExceptionGen.excGen(DummyExceptionGen.java:5)\n" +
					"\tat org.devocative.thallo.test.TestStackTraceProcessor.method3(TestStackTraceProcessor.java:78)\n" +
					"\tat org.devocative.thallo.test.TestStackTraceProcessor.method2(TestStackTraceProcessor.java:67)\n" +
					"\tat org.devocative.thallo.test.TestStackTraceProcessor.method1(TestStackTraceProcessor.java:63)\n" +
					"\tat org.devocative.thallo.test.TestStackTraceProcessor.testStackTraceProcessor_withoutWarp(TestStackTraceProcessor.java:40)\n" +
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
