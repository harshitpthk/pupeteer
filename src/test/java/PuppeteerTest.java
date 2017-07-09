import com.google.gson.JsonObject;
import io.trozo.Puppeteer;
import io.trozo.PuppeteerConfig;
import io.trozo.PuppeteerException;
import io.trozo.PuppeteerImpl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases to verify validation at the time of Initialization
 * Created by harshit.pathak on 23/02/17.
 */
public class PuppeteerTest {
    private TestingServer zkServer;
    private JsonObject configTemplate;
    private JsonObject invalidConfigTemplate;
    private String connectionString;
    private int connectionTimeout;
    private int retryPolicyTimeout;
    private int retryPolicyTimeInterval;
    private Puppeteer puppeteer;
    private String userName;
    private String password;

    @Before
    public void setUp() throws Exception {
        zkServer = new TestingServer(2181, true);
        configTemplate = PuppeteerConfig.getConfiguration("test_config/config_template.json");
        invalidConfigTemplate = PuppeteerConfig.getConfiguration("test_config/invalid_config_template_unknown_keys.json");
        JsonObject zkConfig = PuppeteerConfig.getConfiguration("test_config/zk_config.json");
        connectionString = zkConfig.get("zk_connection_string").getAsString();
        connectionTimeout = zkConfig.get("zk_connection_timeout").getAsInt();
        retryPolicyTimeout = zkConfig.get("zk_retry_policy_max_timeout").getAsInt();
        retryPolicyTimeInterval = zkConfig.get("zk_retry_policy_time_interval").getAsInt();
        userName = zkConfig.get("zk_username").getAsString();
        password = zkConfig.get("zk_password").getAsString();
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
    }

    @After
    public void tearDown() throws Exception {
        zkServer.stop();
    }

    @Test
    public void testPuppeteerDefaultInitialization() throws Exception {
        puppeteer.initialize(connectionString, configTemplate, userName, password);
        Assert.assertTrue(puppeteer.isConnected());
    }
    @Test
    public void testPuppeteerDefaultInitializationWithInvalidKeys() {
        try {
            puppeteer.initialize(connectionString, invalidConfigTemplate, userName, password);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof PuppeteerException.NoValueForKeyException);
        }
        Assert.assertFalse(puppeteer.isConnected());
    }

    @Test
    public void testPuppeteerConnectionTimeoutInitialization() throws Exception {
        puppeteer.initialize(connectionString, configTemplate, connectionTimeout, userName, password);
        Assert.assertTrue(puppeteer.isConnected());
    }

    @Test
    public void testPuppeteerConnectionTimeoutInitializationWithInvalidKeys() {
        try {
            puppeteer.initialize(connectionString, invalidConfigTemplate, connectionTimeout, userName, password);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof PuppeteerException.NoValueForKeyException);
        }
        Assert.assertFalse(puppeteer.isConnected());
    }

    @Test
    public void testPuppeteerGeneralInitialization() throws Exception {
        puppeteer.initialize(connectionString, configTemplate, connectionTimeout, retryPolicyTimeout, retryPolicyTimeInterval, userName, password);
        Assert.assertTrue(puppeteer.isConnected());
    }

    @Test
    public void testPuppeteerGeneralInitializationWithInvalidKeys() {
        try {
            puppeteer.initialize(connectionString, invalidConfigTemplate, connectionTimeout, retryPolicyTimeout, retryPolicyTimeInterval, userName, password);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof PuppeteerException.NoValueForKeyException);
        }
        Assert.assertFalse(puppeteer.isConnected());
    }

    @Test
    public void testGetKey() throws Exception {
        puppeteer.initialize(connectionString, configTemplate, connectionTimeout, retryPolicyTimeout, retryPolicyTimeInterval, userName, password);
        String bar = puppeteer.get("/io/trozo/foo");
        Assert.assertEquals(bar,"bar");
    }

    @Test
    public void testGetKeyWithNullClient() {
        try {
            String bar = puppeteer.get("/io/trozo/foo");
        } catch (Exception e) {
            Assert.assertTrue( e instanceof PuppeteerException.UninitializedException);
        }
    }

    @Test
    public void testGetInvalidKey()  {
        try {
            puppeteer.initialize(connectionString, configTemplate, connectionTimeout, retryPolicyTimeout, retryPolicyTimeInterval, userName, password);
            String bar = puppeteer.get("/bar");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof PuppeteerException.NoValueForKeyException);
        }

    }
}
