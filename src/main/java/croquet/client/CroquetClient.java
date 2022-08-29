package croquet.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import croquet.CroquetModel;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CroquetClient {

    final String protocol;
    final String url;
    final Integer port;
    final String apiKey;
    final String appId;
    final String name;
    final String password;
    final Socket socket;

    final CroquetModel model;

    final Map<Future, Runnable> futures = new HashMap<>();
    final Map<Future, Runnable> futureLoops = new HashMap<>();
    final Map<Subscription, Runnable> subscriptions = new HashMap<>();
    Runnable onConnect = () -> {};
    Consumer<String> onReady = (arg) -> {};

    CroquetClient(String protocol, String url, Integer port, String apiKey, String appId, String name, String password, CroquetModel model) {
        this.protocol = protocol;
        this.url = url;
        this.port = port;
        this.apiKey = apiKey;
        this.appId = appId;
        this.name = name;
        this.password = password;
        this.socket = IO.socket(URI.create(this.protocol + "://" + this.url + ":" + this.port));
        this.model = model;
        this.setup();
    }

    private void setup() {
        this.socket.on("connect", (args -> this.onConnected()));
        this.socket.on("future", (args) -> {
            this.futures.entrySet().stream()
                    .filter(x -> x.getKey().id.equals(args[0]))
                    .findFirst()
                    .ifPresent(x -> x.getValue().run());
            this.futureLoops.entrySet().stream()
                    .filter(x -> x.getKey().id.equals(args[0]))
                    .findFirst()
                    .ifPresent(x -> x.getValue().run());
        });
        this.socket.on("ready", (args -> {
            System.out.println("Ready");
            String model = args[0].toString();
            this.onReady.accept(model);
        }));
        this.socket.on("event", (args -> {
            String scope = args[0].toString();
            String event = args[1].toString();
            Object data = args[2];
            final Subscription s = new Subscription(scope, event);
            //this.subscriptions.keySet().stream().get(event).run();
        }));
    }

    private void onConnected() {
        System.out.println("Connected");
        this.onConnect.run();
        // TODO: Load all futures and subscription and send with join
        final List<Subscription> _subscriptions =
                this.subscriptions.keySet().stream().collect(Collectors.toList());
        final List<Future> _futures =
                this.futures.keySet().stream().collect(Collectors.toList());
        final List<Future> _futureLoops =
                this.futureLoops.keySet().stream().collect(Collectors.toList());
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String subscriptionJson = objectMapper.writeValueAsString(_subscriptions);
            String futuresJson = objectMapper.writeValueAsString(_futures);
            String futureLoopsJson = objectMapper.writeValueAsString(_futureLoops);
            this.socket.emit(
                    "join",
                    this.apiKey,
                    this.appId,
                    this.name,
                    this.password,
                    this.model.toJsonString(),
                    subscriptionJson,
                    futuresJson,
                    futureLoopsJson
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String scope, String event, Runnable onEvent) {
        this.subscriptions.put(new Subscription(scope, event), onEvent);
    }

    public void publish(String scope, String event) {
        this.socket.emit("event", scope, event);
    }

    public void future(String id, int milliseconds, Runnable onFuture) {
        this.futures.put(new Future(id, milliseconds), onFuture);
    }

    public void futureLoop(String id, int milliseconds, Runnable onFuture) {
        this.futureLoops.put(new Future(id, milliseconds), onFuture);
    }

    public void sendLocalPatch(String jsonPatchString) {
        this.socket.emit("update-model", jsonPatchString);
    }

    public void connect(Runnable onConnect, Consumer<String> onReady) {
        this.onConnect = onConnect;
        this.onReady = onReady;
        this.socket.connect();
    }

    public static CroquetClientBuilder builder() {
        return new CroquetClientBuilder();
    }

}
