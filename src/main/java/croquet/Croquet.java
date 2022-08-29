package croquet;

import com.fasterxml.jackson.core.JsonProcessingException;
import croquet.client.CroquetClient;
import croquet.client.CroquetClientBuilder;

import java.lang.reflect.InvocationTargetException;


public class Croquet {
    public static CroquetAppConfigurator configurator() {
        return new CroquetAppConfigurator();
    }
}

