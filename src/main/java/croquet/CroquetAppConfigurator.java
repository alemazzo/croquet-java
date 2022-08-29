package croquet;

import com.fasterxml.jackson.core.JsonProcessingException;
import croquet.client.CroquetClient;

import java.lang.reflect.Type;

public class CroquetAppConfigurator {

    Class<? extends CroquetModel> modelClass;
    Class<? extends CroquetApp<? extends CroquetModel>> appClass;
    String apiKey;
    String appId;
    String session;
    String password;

    String protocol = "ws";
    String url = "localhost";
    Integer port = 3000;

    public CroquetAppConfigurator withModel(Class<? extends CroquetModel> modelClass) {
        this.modelClass = modelClass;
        return this;
    }

    public CroquetAppConfigurator withApp(Class<? extends CroquetApp<? extends CroquetModel>> appClass) {
        this.appClass = appClass;
        return this;
    }

    public CroquetAppConfigurator withApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public CroquetAppConfigurator withAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public CroquetAppConfigurator withSession(String session) {
        this.session = session;
        return this;
    }

    public CroquetAppConfigurator withPassword(String password) {
        this.password = password;
        return this;
    }

    public CroquetAppConfigurator withProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public CroquetAppConfigurator withUrl(String url) {
        this.url = url;
        return this;
    }

    public CroquetAppConfigurator withPort(int port) {
        this.port = port;
        return this;
    }

    public void start() {
        try {
            final CroquetModel templateModel = (CroquetModel) modelClass.getConstructors()[0].newInstance();
            CroquetClient client = CroquetClient.builder()
                    .withProtocol(protocol)
                    .withUrl(url)
                    .withPort(port)
                    .withApiKey(apiKey)
                    .withAppId(appId)
                    .withName(session)
                    .withPassword(password)
                    .withModel(templateModel)
                    .build();
            final CroquetApp<CroquetModel> app = (CroquetApp<CroquetModel>) appClass.getConstructors()[0].newInstance();
            app.setModel(templateModel);
            app.setClient(client);
            app.init();
            app.setOnReady((jsonModel) -> {
                CroquetModel model = this.getModelFromJsonInstance(jsonModel, modelClass);
                app.setModel(model);
            });
            app.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <M extends CroquetModel> M getModelFromJsonInstance(String jsonInstance, Class<M> modelClass) {
        try {
            return CroquetModel.loadFromJson(jsonInstance, modelClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
