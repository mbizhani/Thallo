package org.devocative.thallo.test.init.db;

public class HSQLResource extends AbstractDatabaseResource {
	private final EDbType target;

	public HSQLResource(EDbType target) {
		super(EDbType.HSQLDB);

		this.target = target;
	}

	@Override
	protected String initDB(String server, String username, String password) {
		return String.format("jdbc:hsqldb:mem:%s_db;%s", username, target.getHsqlParam());
	}
}
