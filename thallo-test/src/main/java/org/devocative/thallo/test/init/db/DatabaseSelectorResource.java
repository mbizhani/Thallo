package org.devocative.thallo.test.init.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseSelectorResource extends AbstractDatabaseResource {

	public DatabaseSelectorResource(IDbType dbType) {
		super(dbType);
	}

	// ------------------------------

	@Override
	protected InitDbResult initDB(String username, String password) {
		final AbstractDatabaseResource main;
		try {
			main = (AbstractDatabaseResource) Class.forName(dbConfig.getResource()).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("DatabaseSelector: Can't Create Main DB Resource Class", e);
		}

		try {
			final InitDbResult mainInitDbResult = main.initDB(username, password);
			if (checkDb(main.getDbConfig().getDriverClass(), mainInitDbResult.getJdbcUrl(), username, password)) {
				return mainInitDbResult;
			}
		} catch (Exception e) {
			log.error("DatabaseSelector: Main DB Problem", e);
		}

		final InMemoryResource hsql = new InMemoryResource(main.getDbConfig().getCompatibilityParam());
		return hsql.initDB(username, password);
	}

	// ------------------------------

	private static boolean checkDb(String driverClass, String jdbcUrl, String username, String password) {
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			log.error("DatabaseSelector: Can't Load Main DB Driver Class", e);
			return false;
		}

		try (final Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
			log.info("DatabaseSelector: DB Connected - {}", connection.getMetaData().getDatabaseProductName());
		} catch (Exception e) {
			log.error("DatabaseSelector: Can't Get Connection", e);
			return false;
		}
		return true;
	}
}
