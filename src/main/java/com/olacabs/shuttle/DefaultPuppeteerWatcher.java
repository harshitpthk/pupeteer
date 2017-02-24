package com.olacabs.shuttle;

import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Default Puppeteer Implementation Which is used by default if no external watcher is provided
 * Created by harshit.pathak on 24/02/17.
 */
public class DefaultPuppeteerWatcher implements PuppeteerWatcher{
    private Logger LOGGER = LoggerFactory.getLogger(DefaultPuppeteerWatcher.class);
    private Puppeteer puppeteer;

    public DefaultPuppeteerWatcher(Puppeteer puppeteer) {
        this.puppeteer = puppeteer;
    }

    @Override
    public void process(WatchedEvent event) throws Exception {
        handleEvent(event);
    }

    public String handleEvent(WatchedEvent event) throws Exception {
        LOGGER.info(event.getType().name());
        switch (event.getType()){
            case NodeDataChanged: {
                String newValue = puppeteer.get(event.getPath(),this);
                LOGGER.info(String.format(" %s node data changed to %s", event.getPath(), newValue));
                return newValue;
            }
            case NodeDeleted:{
                LOGGER.info("node deleted " + event.getPath());
                return event.getPath();
            }
            default: {
                return null;
            }
        }
    }
}
