package ez.connection.client;

import java.util.Map;

import ez.connection.queue.messages.ClientConnectionMessageQueue;
import ez.util.Logger;

public class ClientConnectionsListener {

    private ClientConnectionMessageQueue messageReadingQueue;

    private ClientsRegistry connectionsRegister;

    private volatile Thread listenerThread;

    private volatile boolean stopped = false;

    public ClientConnectionsListener(
        ClientConnectionMessageQueue messageReadingQueue,
        ClientsRegistry connectionsRegister
    ) {
        this.messageReadingQueue = messageReadingQueue;
        this.connectionsRegister = connectionsRegister;
    }

    public void start() {

        listenerThread = new Thread(() -> {
            while (!stopped) {
                Map<String, ClientConnection> connections = connectionsRegister.getSnapshot();

                for (ClientConnection connection : connections.values()) {

                    if (connection.newDataAvailable() && !connection.isQueued()) {
                        Logger.log("[CONNECTIONS_LISTENER] new data available - queueing");
                        messageReadingQueue.enqueue(connection);
                        connection.markQueued();
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
