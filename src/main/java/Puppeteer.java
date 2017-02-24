import com.google.gson.JsonObject;

/**
 * Config Manager connecting to zookeeper for application config values.
 * Created by harshit.pathak on 21/02/17.
 */

public interface Puppeteer {
    int DEFAULT_RETRY_POLICY_MAX_TIMEOUT     =   10000;
    int DEFAULT_RETRY_POLICY_TIME_INTERVAL   =   1000;
    int DEFAULT_CONNECTION_MAX_TIMEOUT       =   10000;

    void initialize(String connectionString, JsonObject configTemplate) throws Exception;
    void initialize(String connectionString, JsonObject configTemplate, int maxConnectionTimeout) throws Exception;
    void initialize(String connectionString, JsonObject configTemplate, int maxConnectionTimeout, int retryPolicyMaxTimeout, int retryPolicyTimeInterval) throws Exception;
    void initialize(String connectionString, JsonObject configTemplate, int maxConnectionTimeout, int retryPolicyMaxTimeout, int retryPolicyTimeInterval, PuppeteerWatcher watcher) throws Exception;

    boolean isConnected();
    String get(String key) throws Exception;
    String get(String key, PuppeteerWatcher watcher) throws Exception;

}
