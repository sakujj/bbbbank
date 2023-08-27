package by.sakujj.connection;

;
import by.sakujj.util.PropertiesUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


@Slf4j
public class ConnectionPoolImpl implements ConnectionPool{

    private static ConnectionPoolImpl productionInstance;
    private static ConnectionPoolImpl testInstance;

    private final HikariDataSource dataSource;

    public static final String TEST_PROPERTIES = "test.properties";
    public static final String PROD_PROPERTIES = "application.properties";

    private ConnectionPoolImpl(String propertiesFileName){
        this.propertiesFileName = propertiesFileName;
        Properties properties = PropertiesUtil.newProperties(propertiesFileName);
        dataSource = newHikariDataSource(properties);
    }

    private final String propertiesFileName;

    public static ConnectionPoolImpl getProductionInstance() {
        if (productionInstance == null) {
            productionInstance = new ConnectionPoolImpl(PROD_PROPERTIES);
        }

        return productionInstance;
    }

    public static ConnectionPoolImpl getTestInstance() {
        if (testInstance == null) {
            testInstance = new ConnectionPoolImpl(TEST_PROPERTIES);
        }

        return testInstance;
    }

    @SneakyThrows
    private static HikariDataSource newHikariDataSource(Properties properties) {
        HikariDataSource dataSource = new HikariDataSource();

        Class.forName(properties.getProperty("db.driver.qualified_name"));
        dataSource.setJdbcUrl(properties.getProperty("db.url"));
        dataSource.setUsername(properties.getProperty("db.user"));
        dataSource.setPassword(properties.getProperty("db.password"));

        return dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() {
        dataSource.close();
        log.info("CP associated with '%s' has been closed".formatted(propertiesFileName));
    }
}
