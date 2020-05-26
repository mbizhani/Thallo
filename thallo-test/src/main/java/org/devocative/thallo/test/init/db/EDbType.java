package org.devocative.thallo.test.init.db;

public enum EDbType {
	HSQLDB("org.hsqldb.jdbcDriver", "org.hibernate.dialect.HSQLDialect", "", HSQLResource.class),
	Oracle("oracle.jdbc.OracleDriver", "org.hibernate.dialect.Oracle12cDialect", "sql.syntax_ora=true", OracleDatabaseResource.class),

//	MySQL("", "sql.syntax_mys=true", null),
//	PostgreSQL("", "sql.syntax_pgs=true", null)
	;

	// ------------------------------

	private final String driverClass;
	private final String dialect;
	private final String hsqlParam;
	private final Class<? extends AbstractDatabaseResource> dbResourceClass;

	// ------------------------------

	EDbType(String driverClass, String dialect, String hsqlParam, Class<? extends AbstractDatabaseResource> dbResourceClass) {
		this.driverClass = driverClass;
		this.dialect = dialect;
		this.hsqlParam = hsqlParam;
		this.dbResourceClass = dbResourceClass;
	}

	// ------------------------------

	public String getDriverClass() {
		return driverClass;
	}

	public String getDialect() {
		return dialect;
	}

	public String getHsqlParam() {
		return hsqlParam;
	}

	public Class<? extends AbstractDatabaseResource> getDbResourceClass() {
		return dbResourceClass;
	}
}
