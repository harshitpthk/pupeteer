import com.google.gson.JsonObject;

import java.util.Scanner;

/**
 * Created by harshit.pathak on 23/02/17.
 */
public class Application {

    public static void main(String... args) throws Exception{
        JsonObject configTemplate = PuppeteerConfig.getConfiguration("test_config/config_template.json");
        JsonObject zkConfig = PuppeteerConfig.getConfiguration("test_config/zk_config.json");
        String connectionString = zkConfig.get("zk_connection_string").getAsString();
        Puppeteer puppeteer = new PuppeteerImpl();
        puppeteer.initialize(connectionString,configTemplate);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while(true) {
            System.out.println("input");
            switch (input) {
                case "exit":
                    return ;
                default:
                    input = scanner.nextLine();
            }
        }
    }
}
