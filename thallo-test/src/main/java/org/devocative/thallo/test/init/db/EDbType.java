package org.devocative.thallo.test.init.db;

public enum EDbType {
	HSQLDB("org.hibernate.dialect.HSQLDialect", null, HSQLResource.class),
	Oracle("org.hibernate.dialect.Oracle12cDialect", "sql.syntax_ora=true", OracleDatabaseResource.class),
//	MySQL("", "sql.syntax_mys=true", null),
//	PostgreSQL("", "sql.syntax_pgs=true", null)
	;

	private final String dialect;
	private final String hsqlParam;
	private final Class<? extends AbstractDatabaseResource> dbResourceClass;

	EDbType(String dialect, String hsqlParam, Class<? extends AbstractDatabaseResource> dbResourceClass) {
		this.dialect = dialect;
		this.hsqlParam = hsqlParam;
		this.dbResourceClass = dbResourceClass;
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
