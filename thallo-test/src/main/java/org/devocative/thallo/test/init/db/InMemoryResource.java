package org.devocative.thallo.test.init.db;

public class InMemoryResource extends AbstractDatabaseResource {
	private final String compatibilityParam;

	public InMemoryResource(String compatibilityParam) {
		super(EDbType.InMemoryDB);

		this.compatibilityParam = compatibilityParam;
	}

	@Override
	protected InitDbResult initDB(String username, String password) {
		final String jdbcUrl = getDbConfig().getJdbcUrl().replace("{DB}", username) + ";" + compatibilityParam;
		return new InitDbResult(jdbcUrl, getDbConfig().getDialect(), false);
	}
}
