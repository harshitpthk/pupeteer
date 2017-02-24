import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of Puppeteer
 * Created by harshit.pathak on 21/02/17.
 */

public class PuppeteerImpl implements Puppeteer {
    private static Logger LOGGER = LoggerFactory.getLogger(PuppeteerImpl.class);
    private int maxConnectionTimeout = DEFAULT_CONNECTION_MAX_TIMEOUT;
    private CuratorFramework client;
    private String connectionString;
    private JsonObject configTemplate;
    private RetryUntilElapsed retryPolicy;
    private PuppeteerWatcher puppeteerWatcher;

    @Override
    public void initialize(String connectionString, JsonObject configTemplate) throws Exception {
        initialize(connectionString, configTemplate,DEFAULT_CONNECTION_MAX_TIMEOUT);
    }
    @Override
    public void initialize(String connectionString, JsonObject configTemplate, int maxConnectionTimeout) throws Exception {
        initialize(connectionString, configTemplate, maxConnectionTimeout, DEFAULT_RETRY_POLICY_MAX_TIMEOUT, DEFAULT_RETRY_POLICY_TIME_INTERVAL);
    }
    @Override
    public void initialize(String connectionString, JsonObject configTemplate, int maxConnectionTimeout, int retryPolicyMaxTimeout, int retryPolicyTimeInterval) throws Exception {
        initialize(connectionString,configTemplate,maxConnectionTimeout,retryPolicyMaxTimeout,retryPolicyTimeInterval, new DefaultPuppeteerWatcher(this));
    }

    @Override
    public void initialize(String connectionString, JsonObject configTemplate, int maxConnectionTimeout, int retryPolicyMaxTimeout, int retryPolicyTimeInterval, PuppeteerWatcher watcher) throws Exception {
        this.connectionString = connectionString;
        this.configTemplate = configTemplate;
        this.maxConnectionTimeout = maxConnectionTimeout;
        this.puppeteerWatcher = watcher;
        // Since the application start up will depend on the config template being getting verified we are
        // keeping a retry max elapsed policy for initializing the zookeeper connection.
        retryPolicy = new RetryUntilElapsed(retryPolicyMaxTimeout, retryPolicyTimeInterval);

        // The simplest way to get a CuratorFramework instance. This will use default values.
        // The only required arguments are the connection string and the retry policy
        client = CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
        client.start();
        final List<String> emptyConfigValues = new ArrayList<>();

        LOGGER.info(String.format("Verifying keys present in Zookeeper against the template %s", this.configTemplate.toString()));
        PuppeteerConfig.traverseConfigTree(this.configTemplate, "", new PuppeteerConfig.ConfigTraversalListener() {
            @Override
            public void leafCallback(String leafPath) {
                String zkValue = null;
                try {
                    zkValue = get(leafPath);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    client.close();
                }
                if(zkValue == null || zkValue.isEmpty()){
                    emptyConfigValues.add(leafPath);
                }
                else{
                    LOGGER.info(String.format("Config value for key %s is %s", leafPath, zkValue));
                }
            }
        });

        if(emptyConfigValues.size()>0){
            throw new PuppeteerException.NoValueForKeyException(String.format("no value present in zookeeper for the key %s",
                    Joiner.on("\n").join(emptyConfigValues)));
        }
    }

    @Override
    public boolean isConnected() {
        return client != null && client.getZookeeperClient().isConnected();
    }

    @Override
    public String get(String key) throws Exception {
        return get(key, puppeteerWatcher);
    }

    @Override
    public String get(String key, PuppeteerWatcher watcher) throws Exception {
        if(client == null){
            client = CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
            client.start();
        }
        client.blockUntilConnected(this.maxConnectionTimeout, TimeUnit.MILLISECONDS);
        try {
            return new String(client.getData().usingWatcher(watcher).forPath(key));
        }
        catch (Exception e){
            throw new PuppeteerException.NoValueForKeyException(String.format("no value present in zookeeper for the key %s", key));
        }
    }


}
