package ez.connection.queue.registration;

import java.io.IOException;

import ez.connection.client.ClientConnection;
import ez.connection.client.ClientsRegistry;
import ez.util.Logger;

public class ClientConnectionRegistrationHandler {

    private final ClientConnectionRegistrationQueue queue;

    private ClientsRegistry connectionsRegister;

    private volatile Thread handlerThread;

    private volatile boolean stopped = false;

    public ClientConnectionRegistrationHandler(ClientConnectionRegistrationQueue queue, ClientsRegistry connectionsRegister) {
        this.queue = queue;
        this.connectionsRegister = connectionsRegister;
    }

    public void start() {
        handlerThread = new Thread(() -> {
            while (!stopped) {
                try {
                    ClientConnection connection = queue.dequeue();
                    if (connection != null) {
                        connectionsRegister.registerChannel(connection);
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    // TODO
                }
            }
        });
        handlerThread.start();
    }

    public void stop() throws InterruptedException {
        stopped = true;
        queue.stop();
        handlerThread.join();

        Logger.log("Client connection registrations handler: stopped");
    }
}
