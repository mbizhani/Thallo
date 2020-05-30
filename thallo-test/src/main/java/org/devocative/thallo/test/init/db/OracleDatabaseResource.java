package org.devocative.thallo.test.init.db;

public class OracleDatabaseResource extends AbstractDatabaseResource {
	private static final String[] SYS_GRANTS = {"resource", "connect", "unlimited tablespace", "create view"};

	public OracleDatabaseResource() {
		super(EDbType.Oracle);
	}

	@Override
	protected InitDbResult initDB(String username, String password) throws Exception {

		try (final SqlExec sqlExec = new SqlExec(getDbConfig().getDriverClass(), getDbConfig().getJdbcUrl(),
			"sys as sysdba", "Oradoc_db1")) {

			sqlExec.executeUpdate(String.format("create user %s identified by \"%s\"", username, password));

			for (String sysGrant : SYS_GRANTS) {
				sqlExec.executeUpdate(String.format("grant %s to %s", sysGrant, username));
			}

			return new InitDbResult(getDbConfig().getJdbcUrl(), getDbConfig().getDialect(), true);
		}

	}
}
