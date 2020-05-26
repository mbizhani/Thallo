package org.devocative.thallo.test.init.db;

import org.devocative.thallo.test.init.AbstractResource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public abstract class AbstractDatabaseResource extends AbstractResource {
	public static final String DB_PARAM = "init.db.server";

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd_HHmmss");

	private final EDbType dbType;

	// ------------------------------

	protected AbstractDatabaseResource(EDbType dbType) {
		this.dbType = dbType;
	}

	// ------------------------------

	protected abstract String initDB(String server, String username, String password);

	protected final EDbType getDbType() {
		return dbType;
	}

	protected String getDialect() {
		return dbType.getDialect();
	}

	// ------------------------------

	@Override
	public final Map<String, String> start() {
		final String username = "tst_" + SDF.format(new Date());

		final String jdbcUrl = initDB(System.getProperty(DB_PARAM, "localhost"), username, username).replace("{SERVER}", "localhost");

		return createMapBuilder()
			.put("spring.datasource.url", jdbcUrl)
			.put("spring.datasource.username", username)
			.put("spring.datasource.password", username)
			.put("spring.jpa.hibernate.ddl-auto", "validate")
			.put("spring.jpa.properties.hibernate.dialect", getDialect())
			.put("spring.jpa.open-in-view", "false")
			.get();
	}
}
