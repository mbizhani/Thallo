package org.devocative.thallo.test.init.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.devocative.thallo.test.init.AbstractResource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public abstract class AbstractDatabaseResource extends AbstractResource {
	private static final String JDBC_URL_PARAM = ".jdbc.url";
	private static final String DRIVER_CLASS_PARAM = ".driver.class";
	private static final String DIALECT_PARAM = ".dialect";
	private static final String COMPATIBILITY_PARAM = ".compatibility.param";
	private static final String RESOURCE_PARAM = ".resource";

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd_HHmmss");

	protected final IDbType dbType;
	protected final DbConfig dbConfig;

	// ------------------------------

	protected AbstractDatabaseResource(IDbType dbType) {
		this.dbType = dbType;

		this.dbConfig = new DbConfig(
			cfg(DRIVER_CLASS_PARAM),
			cfg(JDBC_URL_PARAM),
			cfg(DIALECT_PARAM),
			cfg(COMPATIBILITY_PARAM),
			cfg(RESOURCE_PARAM));
	}

	// ------------------------------

	protected abstract InitDbResult initDB(String username, String password) throws Exception;

	protected final DbConfig getDbConfig() {
		return dbConfig;
	}

	// ------------------------------

	@Override
	public final Map<String, String> start() throws Exception {
		final String username = String.format("%s%s_%s",
			getConfig("general.user.prefix"),
			getName(),
			SDF.format(new Date()));

		final InitDbResult initDbResult = initDB(username, username);

		return createMapBuilder()
			.put("spring.datasource.url", initDbResult.getJdbcUrl())
			.put("spring.datasource.username", username)
			.put("spring.datasource.password", username)
			.put("spring.jpa.hibernate.ddl-auto", initDbResult.isValidateSchema() ? "validate" : "create")
			.put("spring.jpa.properties.hibernate.dialect", initDbResult.getDialect())
			.put("spring.jpa.open-in-view", "false")
			.get();
	}

	private String cfg(String param) {
		return getConfig(dbType.getName() + param);
	}

	@Getter
	@RequiredArgsConstructor
	protected static class InitDbResult {
		private final String jdbcUrl;
		private final String dialect;
		private final boolean validateSchema;
	}
}
