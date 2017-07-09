package io.trozo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

/**
 * PuppeteerConfig Class with Util Methods
 * Created by harshit.pathak on 14/12/16.
 */

public class PuppeteerConfig {
    private static Logger LOG = LoggerFactory.getLogger(PuppeteerConfig.class);

    /*
     Method to get configuration exposed as a public method to be part of the Library's Interface.
     Throws @link{ FileNotFoundException} exception in case file not found.
     Return empty object if the file didn't contain valid JSON
     */
    public static JsonObject getConfiguration(String filePath) throws FileNotFoundException {
        File configFile = new File(filePath);
        JsonObject conf = new JsonObject();

        try (Scanner scanner = new Scanner(configFile).useDelimiter("\\A")) {
            String sconf = scanner.next();
            try {
                conf =  (new JsonParser()).parse(sconf).getAsJsonObject();
            }
            catch (Exception jsonParseException){
                LOG.error("Config file is not a valid json file");
            }
        }
        catch (FileNotFoundException e){
            LOG.error("Config file not found " + configFile.getAbsolutePath());
        }
        return conf;
    }

    /*
    Method strictly traverses a tree. If Json Array is found treats the node as leaf.
     */
    public static void traverseConfigTree(JsonObject configTemplate, String root, ConfigTraversalListener listener) throws Exception {

        for(Map.Entry<String,JsonElement> keyValue : configTemplate.entrySet() ){
            if(keyValue.getValue().isJsonNull() || keyValue.getValue().isJsonPrimitive()){
                String leafPath = String.format("%s/%s",root,keyValue.getKey());
                listener.leafCallback(leafPath);
            }
            else{
                if(keyValue.getValue().isJsonObject()) {
                    String nodePath = String.format("%s/%s",root,keyValue.getKey());
                    listener.internalTreeNodeCallback(nodePath);
                    traverseConfigTree(keyValue.getValue().getAsJsonObject(), nodePath , listener);
                }
                else{
                    String leafPath = String.format("%s/%s",root,keyValue.getKey());
                    listener.leafCallback(leafPath);
                }
            }
        }
    }

    public interface ConfigTraversalListener{
        void leafCallback(String leafPath);
        void internalTreeNodeCallback(String nodePath);
    }
}
