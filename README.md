**Puppeteer - Config Manager for Java Applicaitons**

The Client Interface provides the following functionalities.
```
initialize(String connectionString, JsonObject configTemplate)
initialize(String connectionString, JsonObject configTemplate, int maxConnectionTimeout)
initialize(String connectionString, JsonObject configTemplate, int maxConnectionTimeout, int retryPolicyMaxTimeout, int retryPolicyTimeInterval)

isConnected()    // Checks whether the library is connected to Zookeeper or not.
get(String path) // An example zookeeper path is  /a/b/c/d
```

One useful method to parse a Json file to JsonObject is also provided.
