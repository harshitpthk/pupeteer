
import com.google.gson.JsonObject;
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
  private JsonObject configTemplate;
  private JsonObject invalidConfigTemplate;

  @Before
  public void setUp() throws Exception {
    configTemplate = PuppeteerConfig.getConfiguration("test_config/config_template.json");
    invalidConfigTemplate = PuppeteerConfig.getConfiguration("test_config/invalid_config_template.json");
  }

  @After
  public void tearDown() throws Exception {

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
    });
  }

  @Test
  public void testInvalidConfigTraversal() throws Exception {
    try {
      PuppeteerConfig.traverseConfigTree(invalidConfigTemplate, "", new PuppeteerConfig.ConfigTraversalListener() {
        @Override
        public void leafCallback(String leafPath) {
          System.out.println(leafPath);
        }
      });
    }
    catch (PuppeteerException.InvalidPathException e){
      Assert.assertNotNull(e);
    }
  }


}
