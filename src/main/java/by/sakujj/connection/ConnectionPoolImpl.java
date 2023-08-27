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

    private static ConnectionPoolImpl instance;
    private final HikariDataSource dataSource;

    public static final String TEST_PROPERTIES = "test.properties";
    public static final String APP_PROPERTIES = "application.properties";

    private ConnectionPoolImpl(String propertiesFileName){
        currentPropertiesFileName = propertiesFileName;
        Properties properties = PropertiesUtil.newProperties(propertiesFileName);
        dataSource = newHikariDataSource(properties);
    }

    private static String currentPropertiesFileName;

    public static ConnectionPoolImpl getInstance(String propertiesFileName) {
        if (instance == null
                || currentPropertiesFileName.equals(propertiesFileName) == false) {
            if (instance != null)
                instance.close();
            instance = new ConnectionPoolImpl(propertiesFileName);
        }

        return instance;
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
        log.info("CP associated with '%s' has been closed".formatted(currentPropertiesFileName));
    }
}
