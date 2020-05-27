package org.devocative.thallo.test.init.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DbConfig {
	private static final String DB_PARAM = "init.db.server";

	private final String driverClass;
	private final String jdbcUrl;
	private final String dialect;
	private final String compatibilityParam;
	private final String resource;

	public String getJdbcUrl() {
		return jdbcUrl.replace("{SERVER}", System.getProperty(DB_PARAM, "localhost"));
	}
}
