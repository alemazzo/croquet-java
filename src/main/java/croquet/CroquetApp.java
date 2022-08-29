package croquet;

import croquet.client.CroquetClient;

import javax.json.Json;
import javax.json.JsonPatch;
import javax.json.JsonValue;
import java.io.StringReader;
import java.util.Random;
import java.util.function.Consumer;

public abstract class CroquetApp<M extends CroquetModel> {

    Runnable onConnected = () -> {};
    Consumer<String> onReady = (arg) -> {};

    M model;
    protected CroquetClient client;

    Integer futureCounter = 0;

    public final void setModel(M model) {
        this.model = model;
    }

    public final void updateModel(Consumer<M> updateFunction) {
        String prevJson = this.model.toJsonString();
        updateFunction.accept(this.model);
        String resJson = this.model.toJsonString();
        JsonValue source = Json.createReader(new StringReader(prevJson)).readValue();
        JsonValue target = Json.createReader(new StringReader(resJson)).readValue();
        JsonPatch patch = Json.createDiff(source.asJsonObject(), target.asJsonObject());
        String jsonPatchString = patch.toJsonArray().toString();
        System.out.println("JSON PATCH = " + jsonPatchString);
        this.client.sendLocalPatch(jsonPatchString);
        // TODO: implement PATCH
    }

    public final M getModel() {
        return this.model;
    }

    final CroquetApp<M> setClient(CroquetClient client) {
        this.client = client;
        return this;
    }

    final CroquetApp<M> setOnConnected(Runnable onConnected) {
        this.onConnected = onConnected;
        return this;
    }

    final CroquetApp<M> setOnReady(Consumer<String> onReady) {
        this.onReady = onReady;
        return this;
    }

    final CroquetApp<M> connect() {
        this.client.connect(this.onConnected, this.onReady);
        return this;
    }

    public final CroquetApp<M> subscribe(String scope, String event, Runnable onEvent) {
        this.client.subscribe(scope, event, onEvent);
        return this;
    }

    public final CroquetApp<M> publish(String scope, String event) {
        this.client.publish(scope, event);
        return this;
    }

    public final CroquetApp<M> future(int milliseconds, Runnable onFuture) {
        String futureName = this.futureCounter.toString();
        this.futureCounter++;
        this.client.future(futureName, milliseconds, onFuture);
        return this;
    }

    public final CroquetApp<M> futureLoop(int milliseconds, Runnable onFuture) {
        String futureName = this.futureCounter.toString();
        this.futureCounter++;
        this.client.futureLoop(futureName, milliseconds, onFuture);
        return this;
    }


    public abstract void init();
}
