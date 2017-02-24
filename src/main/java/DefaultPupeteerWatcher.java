import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by harshit.pathak on 24/02/17.
 */
public class DefaultPupeteerWatcher implements PuppeteerWatcher{
    private Logger LOGGER = LoggerFactory.getLogger(DefaultPupeteerWatcher.class);
    private Puppeteer puppeteer;

    public DefaultPupeteerWatcher(Puppeteer puppeteer) {
        this.puppeteer = puppeteer;
    }

    @Override
    public void process(WatchedEvent event) throws Exception {
        LOGGER.info(event.getType().name());
        switch (event.getType()){
            case NodeDataChanged: {
                String newValue = puppeteer.get(event.getPath(),this);
                LOGGER.info(String.format(" %s node data changed to %s", event.getPath(), newValue));
                break;
            }
            case NodeDeleted:{
                LOGGER.info("node deleted "+event.getPath());
                break;
            }
            default: {
                break;
            }
        }
    }
}
