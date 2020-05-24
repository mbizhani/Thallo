package org.devocative.thallo.test;

import org.apache.commons.collections.CollectionUtils;
import org.devocative.thallo.common.ExceptionUtil;
import org.devocative.thallo.common.dto.ConstraintViolationDTO;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TAssert {

	public static void assertDbConstraintViolation(String expectedConstraintName, ThrowableCall call) {
		try {
			call.call();

			throw new AssertionError("Exception Expected!");
		} catch (Throwable e) {
			final String constraintName = ExceptionUtil
				.findDbConstraintName(e)
				.orElseThrow(() -> new AssertionError("No Database Constraint Name"));

			if (!expectedConstraintName.equalsIgnoreCase(constraintName)) {
				throw new AssertionError("Expected Constraint Name: " + constraintName);
			}
		}
	}

	public static void assertBeanValidationViolation(ThrowableCall call, ConstraintViolationDTO... expected) {
		try {
			call.call();

			throw new AssertionError("Exception Expected!");
		} catch (javax.validation.ConstraintViolationException e) {
			final List<ConstraintViolationDTO> expectedErrors = Arrays.asList(expected);

			final List<ConstraintViolationDTO> actualErrors = ExceptionUtil.findConstraints(e);

			final Collection remainedExpected = CollectionUtils.subtract(expectedErrors, actualErrors);
			final Collection remainedActual = CollectionUtils.subtract(actualErrors, expectedErrors);

			if (!remainedExpected.isEmpty() || !remainedActual.isEmpty()) {
				throw new AssertionError(String.format("Unmet Expected=[%s], Unmet Actual=[%s]",
					remainedExpected, remainedActual));
			}

		} catch (Throwable e) {
			throw new AssertionError("Expecting javax.validation.ConstraintViolationException");
		}
	}
}
