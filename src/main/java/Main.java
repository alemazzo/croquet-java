import croquet.Croquet;
import croquet.client.CroquetClient;

import java.net.URISyntaxException;
import java.util.Properties;

public class Main {
    static String appId = "io.croquet.hello";
    static String defaultSession = "session";
    static String password = "password";

    public static void main(String[] args) throws URISyntaxException {
        String apiKey = System.getenv("CROQUET_API_KEY");
        int port = 3000;
        String session = defaultSession;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
            if (args.length > 1) {
                session = args[1];
            }
        }

        Croquet.configurator()
                .withPort(port)
                .withApiKey(apiKey)
                .withAppId(appId)
                .withSession(session)
                .withPassword(password)
                .withApp(CounterApp.class)
                .withModel(CounterModel.class)
                .start();

    }

}
