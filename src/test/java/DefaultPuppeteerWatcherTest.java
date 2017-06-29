import com.google.gson.JsonObject;
import com.example.DefaultPuppeteerWatcher;
import com.example.Puppeteer;
import com.example.PuppeteerConfig;
import com.example.PuppeteerImpl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * DefaultPuppeteerWatche
 * Created by harshit.pathak on 24/02/17.
 */
public class DefaultPuppeteerWatcherTest {
    private TestingServer zkServer;
    private Puppeteer puppeteer;

    @Before
    public void setUp() throws Exception {
        zkServer = new TestingServer(2181, true);
        JsonObject configTemplate = PuppeteerConfig.getConfiguration("test_config/config_template.json");
        JsonObject zkConfig = PuppeteerConfig.getConfiguration("test_config/zk_config.json");
        String connectionString = zkConfig.get("zk_connection_string").getAsString();
        int retryPolicyTimeout = zkConfig.get("zk_retry_policy_max_timeout").getAsInt();
        int retryPolicyTimeInterval = zkConfig.get("zk_retry_policy_time_interval").getAsInt();
        String userName = zkConfig.get("zk_username").getAsString();
        String password = zkConfig.get("zk_password").getAsString();
        puppeteer = new PuppeteerImpl();
        final CuratorFramework client = CuratorFrameworkFactory.newClient(connectionString, new RetryUntilElapsed(retryPolicyTimeout, retryPolicyTimeInterval));
        client.start();

        PuppeteerConfig.traverseConfigTree(configTemplate, "", new PuppeteerConfig.ConfigTraversalListener() {

            @Override
            public void leafCallback(String leafPath) {
                try {
                    client.create().forPath(leafPath, "bar".getBytes() );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void internalTreeNodeCallback(String nodePath) {
                try {
                    client.create().forPath(nodePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        puppeteer.initialize(connectionString,configTemplate,userName,password);
    }

    @After
    public void tearDown() throws Exception {
        puppeteer.close();
        zkServer.stop();
    }

    @Test
    public void testHandleEvent() throws Exception{
        Watcher.Event.EventType nodeChangeEvent = Watcher.Event.EventType.NodeDataChanged;
        Watcher.Event.EventType nodeDeletedEvent = Watcher.Event.EventType.NodeDeleted;
        WatchedEvent nodeChangeWatchedEvent = new WatchedEvent(nodeChangeEvent, Watcher.Event.KeeperState.SyncConnected, "/ola/shuttle/foo");
        WatchedEvent nodeDeletedWatchedEvent = new WatchedEvent(nodeDeletedEvent, Watcher.Event.KeeperState.SyncConnected, "/ola/shuttle/foo");
        DefaultPuppeteerWatcher defaultPuppeteerWatcher = new DefaultPuppeteerWatcher(puppeteer);
        String newValue = defaultPuppeteerWatcher.handleEvent(nodeChangeWatchedEvent);
        Assert.assertEquals(newValue, "bar");
        String deletedNode = defaultPuppeteerWatcher.handleEvent(nodeDeletedWatchedEvent);
        Assert.assertEquals(deletedNode,"/ola/shuttle/foo");
    }

}