package org.devocative.thallo.test;

import org.devocative.thallo.test.app.model.User;
import org.devocative.thallo.test.app.model.UserDAO;
import org.devocative.thallo.test.app.model.UserLog;
import org.devocative.thallo.test.app.model.UserLogDAO;
import org.devocative.thallo.test.init.InitRule;
import org.devocative.thallo.test.init.db.EDbType;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class TestDbConstraint {
	private static final String USERNAME = "test";

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private UserLogDAO userLogDAO;

	@Autowired
	private Environment env;

	// ------------------------------

	protected void testUniqueConstraint() {
		userDAO.saveAndFlush(new User().setUsername(USERNAME));

		TAssert.assertDbConstraintViolation(User.UC_OA_USER_USERNAME,
			() -> userDAO.saveAndFlush(new User().setUsername(USERNAME)));
	}

	protected void testRefConstraint() {
		TAssert.assertDbConstraintViolation("fk_userlog2user",
			() -> userLogDAO.saveAndFlush(new UserLog().setUserId(1234L)));
	}

	protected final String getDialect() {
		return env.getProperty("spring.jpa.properties.hibernate.dialect");
	}

	// ------------------------------

	@RunWith(SpringRunner.class)
	@SpringBootTest
	@ActiveProfiles("HSQL")
	public static class TestHSQL extends TestDbConstraint {
		@BeforeClass
		public static void init() {
			System.clearProperty("spring.datasource.url");
		}

		@Test
		@Override
		public void testUniqueConstraint() {
			assertEquals("org.hibernate.dialect.HSQLDialect", getDialect());
			super.testUniqueConstraint();
		}

		@Test
		@Override
		public void testRefConstraint() {
			super.testRefConstraint();
		}
	}

	@RunWith(SpringRunner.class)
	@SpringBootTest
	@ActiveProfiles("H2")
	public static class TestH2 extends TestDbConstraint {
		@BeforeClass
		public static void init() {
			System.clearProperty("spring.datasource.url");
		}

		@Test
		@Override
		public void testUniqueConstraint() {
			assertEquals("org.hibernate.dialect.H2Dialect", getDialect());
			super.testUniqueConstraint();
		}

		// TIP: failed testRefConstraint
	}

	@RunWith(SpringRunner.class)
	@SpringBootTest
	@ActiveProfiles("Oracle")
	public static class TestOracle extends TestDbConstraint {

		@ClassRule
		public static InitRule initRule = new InitRule()
			.enableInMemoryOrRDBMS(EDbType.Oracle);

		@Test
		@Override
		public void testUniqueConstraint() {
			assertEquals("org.hibernate.dialect.Oracle12cDialect", getDialect());
			super.testUniqueConstraint();
		}

		@Test
		@Override
		public void testRefConstraint() {
			super.testRefConstraint();
		}
	}

	@RunWith(SpringRunner.class)
	@SpringBootTest
	@ActiveProfiles("Postgres")
	public static class TestPostgres extends TestDbConstraint {

		@ClassRule
		public static GenericContainer<?> DB_CONTAINER = new GenericContainer<>("postgres:12.2")
			.withExposedPorts(5432)
			.withEnv("POSTGRES_PASSWORD", "postgres");

		@BeforeClass
		public static void init() {
			TestDbConstraint.setDbParams("jdbc:postgresql://localhost:%s/postgres", 5432, DB_CONTAINER);
		}

		@Test
		@Override
		public void testUniqueConstraint() {
			assertEquals("org.hibernate.dialect.PostgreSQL95Dialect", getDialect());
			super.testUniqueConstraint();
		}

		@Test
		@Override
		public void testRefConstraint() {
			super.testRefConstraint();
		}
	}

	@RunWith(SpringRunner.class)
	@SpringBootTest
	@ActiveProfiles("Mysql8")
	public static class TestMysql8 extends TestDbConstraint {

		@ClassRule
		public static GenericContainer<?> DB_CONTAINER = new GenericContainer<>("mysql:8.0.19")
			.withExposedPorts(3306)
			.withCommand("--default-authentication-plugin=mysql_native_password")
			.withEnv("MYSQL_ROOT_PASSWORD", "root")
			.withEnv("MYSQL_DATABASE", "test")
			.withEnv("MYSQL_USER", "test")
			.withEnv("MYSQL_PASSWORD", "test");

		@BeforeClass
		public static void init() {
			TestDbConstraint.setDbParams("jdbc:mysql://localhost:%s/test", 3306, DB_CONTAINER);
		}

		@Test
		@Override
		public void testUniqueConstraint() {
			assertEquals("org.hibernate.dialect.MySQL8Dialect", getDialect());
			super.testUniqueConstraint();
		}

		// TIP: failed testRefConstraint
	}

	// ------------------------------

	private static void setDbParams(String url, int port, GenericContainer<?> container) {
		System.setProperty("spring.datasource.url", String.format(url, container.getMappedPort(port)));
	}
}
