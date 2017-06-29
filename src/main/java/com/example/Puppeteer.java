package com.example;

import com.google.gson.JsonObject;

/**
 * Config Manager connecting to zookeeper for application config values.
 * Created by harshit.pathak on 21/02/17.
 */

public interface Puppeteer {
    int DEFAULT_RETRY_POLICY_MAX_TIMEOUT     =   4000;
    int DEFAULT_RETRY_POLICY_TIME_INTERVAL   =   500;
    int DEFAULT_CONNECTION_MAX_TIMEOUT       =   4000;

    void initialize(String connectionString, JsonObject configTemplate, String userName, String password) throws Exception;
    void initialize(String connectionString, JsonObject configTemplate, int maxConnectionTimeout,  String userName, String password) throws Exception;
    void initialize(String connectionString, JsonObject configTemplate, int maxConnectionTimeout, int retryPolicyMaxTimeout, int retryPolicyTimeInterval,  String userName, String password) throws Exception;
    void initialize(String connectionString, JsonObject configTemplate, int maxConnectionTimeout, int retryPolicyMaxTimeout, int retryPolicyTimeInterval,  String userName, String password, PuppeteerWatcher watcher) throws Exception;

    boolean isConnected();
    String get(String key) throws Exception;
    String get(String key, PuppeteerWatcher watcher) throws Exception;

    void close() throws Exception;
}
