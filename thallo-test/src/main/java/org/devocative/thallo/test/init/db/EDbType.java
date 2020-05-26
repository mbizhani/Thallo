package org.devocative.thallo.test.init.db;

public enum EDbType {
	InMemoryDB(
		"org.hsqldb.jdbcDriver",
		"org.hibernate.dialect.HSQLDialect",
		"",
		InMemoryDB.class,
		"org.hibernate.dialect.HSQLDialect"),
	/*InMemoryDB(
		"org.h2.Driver",
		"org.hibernate.dialect.H2Dialect",
		"",
		InMemoryDB.class,
		"org.hibernate.dialect.H2Dialect"),*/
	Oracle(
		"oracle.jdbc.OracleDriver",
		"org.hibernate.dialect.Oracle12cDialect",
//		"Mode=Oracle",
		"sql.syntax_ora=true",
		OracleDatabaseResource.class,
		"org.devocative.thallo.test.init.dialect.HSQL4Oracle")

//	MySQL("", "sql.syntax_mys=true", null),
//	PostgreSQL("", "sql.syntax_pgs=true", null)
	;

	// ------------------------------

	private final String driverClass;
	private final String dialect;
	private final String compatibilityParam;
	private final Class<? extends AbstractDatabaseResource> dbResourceClass;
	private final String hsqlEqvDialect;

	// ------------------------------

	EDbType(String driverClass, String dialect, String compatibilityParam, Class<? extends AbstractDatabaseResource> dbResourceClass, String hsqlEqvDialect) {
		this.driverClass = driverClass;
		this.dialect = dialect;
		this.compatibilityParam = compatibilityParam;
		this.dbResourceClass = dbResourceClass;
		this.hsqlEqvDialect = hsqlEqvDialect;
	}

	// ------------------------------

	public String getDriverClass() {
		return driverClass;
	}

	public String getDialect() {
		return dialect;
	}

	public String getCompatibilityParam() {
		return compatibilityParam;
	}

	public Class<? extends AbstractDatabaseResource> getDbResourceClass() {
		return dbResourceClass;
	}

	public String getHsqlEqvDialect() {
		return hsqlEqvDialect;
	}
}
