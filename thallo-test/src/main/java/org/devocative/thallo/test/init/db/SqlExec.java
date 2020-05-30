package org.devocative.thallo.test.init.db;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class SqlExec implements AutoCloseable {
	private final Connection connection;

	public SqlExec(String driverClass, String url, String username, String password) {

		try {
			Class.forName(driverClass);
			connection = DriverManager.getConnection(url, username, password);
			log.info("SqlExec - Connection Created: URL=[{}]", url);
		} catch (Exception e) {
			log.error("SqlExec: Connection Creation", e);
			throw new RuntimeException(e);
		}
	}

	public int executeUpdate(String sql) throws SQLException {
		final Statement statement = connection.createStatement();
		return statement.executeUpdate(sql);
	}

	public void close() throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
}
