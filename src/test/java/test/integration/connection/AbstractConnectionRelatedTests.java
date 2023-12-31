package test.integration.connection;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.connection.ConnectionPoolImpl;
import by.sakujj.context.ApplicationContext;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.sql.Connection;
import java.sql.SQLException;

@Getter
public class AbstractConnectionRelatedTests {
    private static final ApplicationContext context = ApplicationContext.getTestInstance();

    private static final ConnectionPool connectionPool
            = context.getByClass(ConnectionPool.class);

    private boolean doRollback = false;
    private Connection connection = null;

    @BeforeEach
    void openConnection(TestInfo testInfo) throws SQLException {
        connection = connectionPool.getConnection();

        doRollback = testInfo.getTestMethod().get().getAnnotation(Rollback.class) != null;

        if (doRollback) {
            connection.setAutoCommit(false);
        }
    }


    @AfterEach
    void closeConnection() throws SQLException {
        if (doRollback) {
            connection.rollback();
            connection.setAutoCommit(true);
        }

        connection.close();
    }
}
