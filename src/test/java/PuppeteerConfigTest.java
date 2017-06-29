import com.google.gson.JsonObject;
import com.example.PuppeteerConfig;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * Config Util class test cases
 * Created by harshit.pathak on 16/02/17.
 */

public class PuppeteerConfigTest {
    private TestingServer zkServer;
    private JsonObject configTemplate;
    private JsonObject configTemplateWithArray;

    @Before
    public void setUp() throws Exception {
        zkServer = new TestingServer(2181, true);
        configTemplate = PuppeteerConfig.getConfiguration("test_config/config_template.json");
        configTemplateWithArray = PuppeteerConfig.getConfiguration("test_config/config_template_with_array.json");
    }

    @After
    public void tearDown() throws Exception {
        zkServer.stop();
    }

    @Test
    public void testGetConfiguration() throws Exception {
        PuppeteerConfig unusedPuppeteerConfigObject = new PuppeteerConfig();
        JsonObject emptyConfig = PuppeteerConfig.getConfiguration("test_config/zk_invalid_config.json");
        Assert.assertTrue(emptyConfig.entrySet().isEmpty());
        JsonObject testConfig = PuppeteerConfig.getConfiguration("test_config/zk_config.json");
        Assert.assertFalse(testConfig.entrySet().isEmpty());
        Assert.assertEquals(testConfig.get("zk_connection_timeout").getAsInt(), 10000);
    }
    @Test
    public void testGetUnknownConfiguration() {
        try {
            JsonObject unknownConfig = PuppeteerConfig.getConfiguration("test_config/unknown_configuration.json");
        } catch (FileNotFoundException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testConfigTraversal() throws Exception {
        PuppeteerConfig.traverseConfigTree(configTemplate, "", new PuppeteerConfig.ConfigTraversalListener() {
            @Override
            public void leafCallback(String leafPath) {
              Assert.assertNotNull(leafPath);
              System.out.println(leafPath);
            }

            @Override
            public void internalTreeNodeCallback(String nodePath) {
                System.out.println(nodePath);
            }
        });
    }

    @Test
    public void testConfigWithArrayTraversal() throws Exception {
        PuppeteerConfig.traverseConfigTree(configTemplateWithArray, "", new PuppeteerConfig.ConfigTraversalListener() {
            @Override
            public void leafCallback(String leafPath) {
              System.out.println(leafPath);
            }

            @Override
            public void internalTreeNodeCallback(String nodePath) {
                System.out.println(nodePath);
            }
        });

    }


}
