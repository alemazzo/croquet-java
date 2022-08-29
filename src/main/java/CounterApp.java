import croquet.CroquetApp;
import croquet.CroquetModel;

import static java.lang.Thread.sleep;

public class CounterApp extends CroquetApp<CounterModel> {

    @Override
    public void init() {
        future(500, this::welcomeMessage);
        futureLoop(500, this::incrementCounter);
        //subscribe("counter", "increment", this::incrementCounter);
    }

    private void welcomeMessage() {
        System.out.println("Welcome");
    }

    private void incrementCounter() {
        this.updateModel((model) -> {
            model.counter++;
        });
        System.out.println("Counter = " + this.getModel().counter);
    }
}
