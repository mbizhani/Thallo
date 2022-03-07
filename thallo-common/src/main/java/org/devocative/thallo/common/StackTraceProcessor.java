package org.devocative.thallo.common;

import java.util.HashSet;
import java.util.Set;

public class StackTraceProcessor {

	public static String filter(Throwable throwable, Class<?> byClass) {
		return filter(throwable, findBasePackage(byClass));
	}

	public static String filter(Throwable throwable, String... filters) {
		StringBuilder builder = new StringBuilder();
		processStackTrace(true, throwable, builder, filters);
		return builder.toString();
	}

	// ------------------------------

	private static String findBasePackage(Class<?> cls) {
		String result = "";
		final String[] split = cls.getName().split("\\.");
		if (split.length == 2) {
			result = split[0];
		} else if (split.length > 2) {
			result = split[0] + "." + split[1];
		}
		return result;
	}

	private static void processStackTrace(boolean isFirstLevel, Throwable th, StringBuilder writer, String... filters) {
		if (!isFirstLevel) {
			writer.append("Caused by: ");
		}
		writer.append(th.toString()).append("\n");

		final StackTraceElement[] elements = th.getStackTrace();

		if (th.getCause() != null) {
			for (int i = 0; i < elements.length && i < 2; i++) {
				appendElement(elements[i], writer);
			}
			writer.append("\t...\n");
			processStackTrace(false, th.getCause(), writer, filters);
		} else {
			processStackTraceElements(elements, writer, filters);
		}
	}

	private static void processStackTraceElements(StackTraceElement[] elements, StringBuilder writer, String... filters) {
		appendElement(elements[0], writer);

		Set<Integer> indexOfPrintedElements = new HashSet<>();
		indexOfPrintedElements.add(0);

		boolean skipped = false;

		for (int i = 1; i < elements.length; i++) {
			final StackTraceElement element = elements[i];
			if (isValid(element, filters)) {
				if (skipped) {
					writer.append("\t...\n");
					skipped = false;
				}

				if (!indexOfPrintedElements.contains(i - 1)) {
					appendElement(elements[i - 1], writer);
				}
				appendElement(element, writer);
				indexOfPrintedElements.add(i);
			} else {
				skipped = true;
			}
		}

		if (skipped) {
			writer.append("\t...\n");
		}
	}

	private static boolean isValid(StackTraceElement ste, String... filters) {
		String st = ste.toString();
		for (String filter : filters) {
			if (st.contains(filter)) {
				return true;
			}
		}
		return false;
	}

	private static void appendElement(StackTraceElement element, StringBuilder writer) {
		writer.append("\tat ").append(element.toString()).append("\n");
	}
}
