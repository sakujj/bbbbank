package test.integration.listeners;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import test.integration.connection.TestConnectionPool;


@Slf4j
public class CloseTestConnectionPoolListener implements TestExecutionListener {
    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        TestConnectionPool.getInstance().close();
        log.debug("Connection pool has been closed and all connections have been released");
    }
}
