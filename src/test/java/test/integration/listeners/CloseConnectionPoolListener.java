package test.integration.listeners;
import by.sakujj.connection.ConnectionPoolImpl;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;


public class CloseConnectionPoolListener implements TestExecutionListener {
    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        ConnectionPoolImpl.getInstance(ConnectionPoolImpl.TEST_PROPERTIES).close();
    }
}
