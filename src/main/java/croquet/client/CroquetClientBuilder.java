package croquet.client;

import croquet.CroquetModel;

public class CroquetClientBuilder {
    String protocol = "ws";
    String url = "localhost";
    Integer port = 3000;
    String apiKey;
    String appId;
    String name;
    String password;
    CroquetModel model;

    public CroquetClientBuilder withProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public CroquetClientBuilder withUrl(String url) {
        this.url = url;
        return this;
    }

    public CroquetClientBuilder withPort(Integer port) {
        this.port = port;
        return this;
    }

    public CroquetClientBuilder withApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public CroquetClientBuilder withAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public CroquetClientBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CroquetClientBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public CroquetClientBuilder withModel(CroquetModel model) {
        this.model = model;
        return this;
    }


    public CroquetClient build() {
        return new CroquetClient(this.protocol, this.url, this.port, this.apiKey, this.appId, this.name, this.password, this.model);
    }

}
