package org.devocative.thallo.test.init.db;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class SqlExec {
	private final String url;
	private final Connection connection;

	public SqlExec(String driverClass, String url, String username, String password) {
		this.url = url;

		try {
			Class.forName(driverClass);
			connection = DriverManager.getConnection(url, username, password);
			log.info("SqlExec - Connection Created: URL=[{}]", url);
		} catch (Exception e) {
			log.error("SqlExec: Connection Creation", e);
			throw new RuntimeException(e);
		}
	}

	public String getUrl() {
		return url;
	}

	public Connection getConnection() {
		return connection;
	}

	public int executeUpdate(String sql) {
		try {
			final Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			log.error("SqlExec.executeUpdate", e);
			throw new RuntimeException(e);
		}
	}

	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				log.error("SqlExec.close", e);
				throw new RuntimeException(e);
			}
		}
	}
}
