package test.integration.connection;

import by.sakujj.connectionpool.ConnectionPool;
import by.sakujj.exceptions.DAOException;
import by.sakujj.util.PropertiesUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class TestConnectionPool implements ConnectionPool {
    private static final String PROPERTY_FILE = "test.properties";
    private static final Properties PROPERTIES = PropertiesUtil.newProperties(PROPERTY_FILE);

    private static final String URL_KEY = PROPERTIES.getProperty("db.url");
    private static final String USER_KEY = PROPERTIES.getProperty("db.user");
    private static final String PASSWORD_KEY = PROPERTIES.getProperty("db.password");
    private static final String DRIVER_KEY = PROPERTIES.getProperty("db.driver.qualified_name");
    private static final int MAX_POOL_SIZE;
    private static final int MIN_IDLE;
    private static final int MAX_LIFETIME;
    private static final int IDLE_TIMEOUT;

    static {
        String property = PROPERTIES.getProperty("db.pool.max_size");
        if (property == null) {
            MAX_POOL_SIZE = 10;
        } else {
            MAX_POOL_SIZE = Integer.parseInt(property);
        }

        property = PROPERTIES.getProperty("db.pool.min_idle");
        if (property == null) {
            MIN_IDLE = 10;
        } else {
            MIN_IDLE = Integer.parseInt(property);
        }

        property = PROPERTIES.getProperty("db.pool.max_lifetime");
        if (property == null) {
            MAX_LIFETIME = 1_800_000;
        } else {
            MAX_LIFETIME = Integer.parseInt(property);
        }

        property = PROPERTIES.getProperty("db.pool.idle_timeout");
        if (property == null) {
            IDLE_TIMEOUT = 600_000;
        } else {
            IDLE_TIMEOUT = Integer.parseInt(property);
        }
    }


    private static final TestConnectionPool INSTANCE = new TestConnectionPool();
    private static final HikariDataSource dataSource = newHikariDataSource();

    private TestConnectionPool() {
    }

    public static TestConnectionPool getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws DAOException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        } else {
            throw new RuntimeException("Cant close");
        }
    }

    @SneakyThrows
    private static HikariDataSource newHikariDataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        Class.forName(DRIVER_KEY);
        dataSource.setJdbcUrl(URL_KEY);
        dataSource.setUsername(USER_KEY);
        dataSource.setPassword(PASSWORD_KEY);
        dataSource.setMaximumPoolSize(MAX_POOL_SIZE);
        dataSource.setMinimumIdle(MIN_IDLE);
        dataSource.setMaxLifetime(MAX_LIFETIME);
        dataSource.setIdleTimeout(IDLE_TIMEOUT);

        return dataSource;
    }
}
