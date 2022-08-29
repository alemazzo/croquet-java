package croquet.client;

public class Subscription {
    public String scope;
    public String event;

    Subscription(String scope, String event) {
        this.scope = scope;
        this.event = event;
    }
}
