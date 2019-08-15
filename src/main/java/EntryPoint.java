import ez.EZMessenger;
import ez.EZMessenger.Configurator;

public class EntryPoint {

    public static void main(String[] args) throws InterruptedException {

        EZMessenger ezMessenger = Configurator.configureDefault();
        ezMessenger.start();

        Thread.currentThread().join();
    }
}
