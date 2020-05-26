package org.devocative.thallo.test.init.dialect;

import org.hibernate.dialect.HSQLDialect;

import java.sql.Types;

public class HSQL4Oracle extends HSQLDialect {
	public HSQL4Oracle() {
		super();

		registerColumnType(Types.BIGINT, "NUMERIC(19,0)");
		registerColumnType(Types.INTEGER, "NUMERIC(10,0)");
		registerColumnType(Types.TINYINT, "NUMERIC(3,0)");

//		registerColumnType(Types.DATE, "DATE");
//		registerColumnType(Types.TIME, "DATE");
//		registerColumnType(Types.TIMESTAMP, "TIMESTAMP");
	}
}
