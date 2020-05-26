package org.devocative.thallo.test.init.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseSelectorResource extends AbstractDatabaseResource {
	private static final Logger log = LoggerFactory.getLogger(DatabaseSelectorResource.class);

	private String dialect;

	// ------------------------------

	public DatabaseSelectorResource(EDbType dbType) {
		super(dbType);
	}

	// ------------------------------

	@Override
	protected String initDB(String server, String username, String password) {
		final AbstractDatabaseResource main;
		try {
			main = getDbType().getDbResourceClass().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("DatabaseSelector: Can't Create Main DB Resource", e);
		}

		try {
			final String mainJdbcUrl = main.initDB(server, username, password);
			if (checkDb(mainJdbcUrl, username, password)) {
				dialect = getDbType().getDialect();
				return mainJdbcUrl;
			}
		} catch (Exception e) {
			log.error("DatabaseSelector: Main DB Problem", e);
		}

		dialect = EDbType.HSQLDB.getDialect();
		final HSQLResource hsql = new HSQLResource(getDbType());
		return hsql.initDB(server, username, password);
	}

	@Override
	protected String getDialect() {
		return dialect;
	}

	// ------------------------------

	private boolean checkDb(String url, String username, String password) {
		try {
			Class.forName(getDbType().getDriverClass());
		} catch (ClassNotFoundException e) {
			log.error("DatabaseSelector: Can't Load Main DB Driver Class", e);
			return false;
		}

		try (final Connection connection = DriverManager.getConnection(url, username, password)) {
			log.info("DatabaseSelector: DB Connected - {}", connection.getMetaData().getDatabaseProductName());
		} catch (Exception e) {
			log.error("DatabaseSelector: Can't Get Connection", e);
			return false;
		}
		return true;
	}
}
