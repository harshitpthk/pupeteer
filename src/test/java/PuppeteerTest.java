import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases to verify validation at the time of Initialization
 * Created by harshit.pathak on 23/02/17.
 */
public class PuppeteerTest {
    private JsonObject configTemplate;
    private JsonObject invalidConfigTemplate;
    private String connectionString;
    private int connectionTimeout;
    private int retryPolicyTimeout;
    private int retryPolicyTimeInterval;
    private Puppeteer puppeteer;

    @Before
    public void setUp() throws Exception {
        configTemplate = PuppeteerConfig.getConfiguration("test_config/config_template.json");
        invalidConfigTemplate = PuppeteerConfig.getConfiguration("test_config/invalid_config_template_unknown_keys.json");
        JsonObject zkConfig = PuppeteerConfig.getConfiguration("test_config/zk_config.json");
        connectionString = zkConfig.get("zk_connection_string").getAsString();
        connectionTimeout = zkConfig.get("zk_connection_timeout").getAsInt();
        retryPolicyTimeout = zkConfig.get("zk_retry_policy_max_timeout").getAsInt();
        retryPolicyTimeInterval = zkConfig.get("zk_retry_policy_time_interval").getAsInt();
        puppeteer = new PuppeteerImpl();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testPuppeteerDefaultInitialization() throws Exception {
        puppeteer.initialize(connectionString, configTemplate);
        Assert.assertTrue(puppeteer.isConnected());
    }
    @Test
    public void testPuppeteerDefaultInitializationWithInvalidKeys() {
        try {
            puppeteer.initialize(connectionString, invalidConfigTemplate);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof PuppeteerException.NoValueForKeyException);
        }
        Assert.assertFalse(puppeteer.isConnected());
    }

    @Test
    public void testPuppeteerConnectionTimeoutInitialization() throws Exception {
        puppeteer.initialize(connectionString, configTemplate, connectionTimeout);
        Assert.assertTrue(puppeteer.isConnected());
    }

    @Test
    public void testPuppeteerConnectionTimeoutInitializationWithInvalidKeys() {
        try {
            puppeteer.initialize(connectionString, invalidConfigTemplate, connectionTimeout);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof PuppeteerException.NoValueForKeyException);
        }
        Assert.assertFalse(puppeteer.isConnected());
    }

    @Test
    public void testPuppeteerGeneralInitialization() throws Exception {
        puppeteer.initialize(connectionString, configTemplate, connectionTimeout, retryPolicyTimeout, retryPolicyTimeInterval);
        Assert.assertTrue(puppeteer.isConnected());
    }

    @Test
    public void testPuppeteerGeneralInitializationWithInvalidKeys() {
        try {
            puppeteer.initialize(connectionString, invalidConfigTemplate, connectionTimeout, retryPolicyTimeout, retryPolicyTimeInterval);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof PuppeteerException.NoValueForKeyException);
        }
        Assert.assertFalse(puppeteer.isConnected());
    }

    @Test
    public void testGetKey() throws Exception {
        puppeteer.initialize(connectionString, configTemplate, connectionTimeout, retryPolicyTimeout, retryPolicyTimeInterval);
        String bar = puppeteer.get("/ola/shuttle/foo");
        Assert.assertEquals(bar,"bar");
    }

    @Test
    public void testGetInvalidKey()  {
        try {
            puppeteer.initialize(connectionString, configTemplate, connectionTimeout, retryPolicyTimeout, retryPolicyTimeInterval);
            String bar = puppeteer.get("/bar");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof PuppeteerException.NoValueForKeyException);
        }

    }
}