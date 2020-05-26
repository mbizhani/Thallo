package org.devocative.thallo.test.init.db;

public class OracleDatabaseResource extends AbstractDatabaseResource {
	private static final String[] SYS_GRANTS = {"resource", "connect", "unlimited tablespace", "create view"};

	private SqlExec sqlExec;

	public OracleDatabaseResource() {
		super(EDbType.Oracle);
	}

	@Override
	protected String initDB(String server, String username, String password) {
		final String url = String.format("jdbc:oracle:thin:@%s:1521/orclpdb1.localdomain", server);

		sqlExec = new SqlExec(getDbType().getDriverClass(), url, "sys as sysdba", "Oradoc_db1");
		sqlExec.executeUpdate(String.format("create user %s identified by \"%s\"", username, password));

		for (String sysGrant : SYS_GRANTS) {
			sqlExec.executeUpdate(String.format("grant %s to %s", sysGrant, username));
		}
		return url;
	}

	@Override
	public void stop() {
		sqlExec.close();

		super.stop();
	}
}
