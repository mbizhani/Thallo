package org.devocative.thallo.common;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

public class ExceptionUtil {
	private static final String H2_UNIQUE_CONST_SPLITTER = "_index_";

	public static Optional<String> findDbConstraintName(Throwable ex) {
		if (ex instanceof DataIntegrityViolationException) {
			return findDbConstraintName(ex.getCause());
		} else if (ex instanceof ConstraintViolationException) {
			final ConstraintViolationException cve = (ConstraintViolationException) ex;

			String constraintName = cve.getConstraintName();
			if (constraintName != null) {
				constraintName = constraintName.toLowerCase();

				if (constraintName.contains(".")) {
					constraintName = constraintName.split("\\.")[1];

					if (constraintName.contains(H2_UNIQUE_CONST_SPLITTER)) { // FOR H2
						final int idx = constraintName.lastIndexOf(H2_UNIQUE_CONST_SPLITTER);
						constraintName = constraintName.substring(0, idx);
					}
				}

				return Optional.of(constraintName);
			}
			return Optional.empty();
		}

		return Optional.empty();
	}

}
