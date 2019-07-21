package ez.connection.client;

import java.util.Map;

import ez.connection.queue.messages.ClientConnectionMessageQueue;
import ez.util.Logger;

public class ClientConnectionsListener {

    private ClientConnectionMessageQueue messageReadingQueue;

    private ClientConnectionsRegister connectionsRegister;

    private volatile Thread listenerThread;

    private volatile boolean stopped = false;

    public ClientConnectionsListener(
        ClientConnectionMessageQueue messageReadingQueue,
        ClientConnectionsRegister connectionsRegister
    ) {
        this.messageReadingQueue = messageReadingQueue;
        this.connectionsRegister = connectionsRegister;
    }

    public void start() {

        listenerThread = new Thread(() -> {
            while (!stopped) {
                Map<String, ClientConnection> connections = connectionsRegister.getSnapshot();

                for (ClientConnection connection : connections.values()) {
                    if (connection.newDataAvailable()) {
                        messageReadingQueue.add(connection);
                    }
                }
            }
        });

        listenerThread.start();
    }

    public void stop() throws InterruptedException {
        stopped = true;
        listenerThread.join();

        Logger.log("Client connections listener: stopped");
    }
}
