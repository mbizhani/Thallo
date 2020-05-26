package org.devocative.thallo.test.init.db;

public class InMemoryDB extends AbstractDatabaseResource {
	private final EDbType target;

	public InMemoryDB(EDbType target) {
		super(EDbType.InMemoryDB);

		this.target = target;
	}

	@Override
	protected String initDB(String server, String username, String password) {
		return String.format("jdbc:hsqldb:mem:%s_db;%s", username, target.getCompatibilityParam());
//		return String.format("jdbc:h2:mem:%s_db;%s", username, target.getCompatibilityParam());
	}
}
