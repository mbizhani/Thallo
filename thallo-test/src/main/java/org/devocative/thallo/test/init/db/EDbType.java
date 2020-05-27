package org.devocative.thallo.test.init.db;

public enum EDbType implements IDbType {
	InMemoryDB("hsql"),
	Oracle("oracle")

//	MySQL("", "sql.syntax_mys=true", null),
//	PostgreSQL("", "sql.syntax_pgs=true", null)
	;

	// ------------------------------

	private final String name;

	// ------------------------------

	EDbType(String name) {
		this.name = name;
	}

	// ------------------------------

	@Override
	public String getName() {
		return name;
	}
}
