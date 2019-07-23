import ez.EZMessenger;
import ez.EZMessenger.Configurator;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        EZMessenger ezMessenger = Configurator.configureDefault();
        ezMessenger.start();

        Thread.currentThread().join();
    }
}
