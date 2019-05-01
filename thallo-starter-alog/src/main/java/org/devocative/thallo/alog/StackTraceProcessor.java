package org.devocative.thallo.alog;

import java.util.HashSet;
import java.util.Set;

public class StackTraceProcessor {
	private final Throwable throwable;
	private final String filter;

	// ------------------------------

	public StackTraceProcessor(Throwable throwable, Class byClass) {
		this(throwable, findBasePackage(byClass));
	}

	public StackTraceProcessor(Throwable throwable, String filter) {
		this.throwable = throwable;
		this.filter = filter;
	}

	// ------------------------------

	public String process() {
		StringBuilder builder = new StringBuilder();
		builder.append(throwable.toString()).append("\n");
		processStackTraceElements(throwable.getStackTrace(), builder);
		processStackTrace(throwable.getCause(), builder);

		return builder.toString();
	}

	// ------------------------------

	private static String findBasePackage(Class cls) {
		String result = "";
		final String[] split = cls.getName().split("\\.");
		if (split.length == 2) {
			result = split[0];
		} else if (split.length > 2) {
			result = split[0] + "." + split[1];
		}
		return result;
	}

	private void processStackTrace(Throwable th, StringBuilder writer) {
		if (th != null) {
			writer.append("Caused by: ").append(th.toString()).append("\n");
			processStackTraceElements(th.getStackTrace(), writer);
			processStackTrace(th.getCause(), writer);
		}
	}

	private void processStackTraceElements(StackTraceElement[] elements, StringBuilder writer) {
		Set<Integer> printedElements = new HashSet<>();
		printedElements.add(0);

		for (int i = 0; i < elements.length; i++) {
			if (i == 0) {
				writer.append("\tat ").append(elements[i].toString()).append("\n");
			} else if (isValid(elements[i])) {
				if (!printedElements.contains(i - 1)) {
					writer.append("\tat ").append(elements[i - 1].toString()).append("\n");
				}
				writer.append("\tat ").append(elements[i].toString()).append("\n");
				printedElements.add(i);
			}
		}
	}

	private boolean isValid(StackTraceElement ste) {
		return ste.toString().contains(filter);
	}
}
