package ez.connection.listener;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import ez.connection.client.ClientConnection;
import ez.connection.data.ConnectionMessage;
import ez.connection.queue.messages.ClientConnectionMessageQueue;
import ez.connection.registry.ConnectionsRegistry;
import ez.util.Logger;

public class ConnectionsListener {

    private ClientConnectionMessageQueue messageReadingQueue;

    private ConnectionsRegistry connectionsRegister;

    private volatile Thread listenerThread;

    private volatile boolean stopped = false;

    public ConnectionsListener(
        ClientConnectionMessageQueue messageReadingQueue,
        ConnectionsRegistry connectionsRegister
    ) {
        this.messageReadingQueue = messageReadingQueue;
        this.connectionsRegister = connectionsRegister;
    }

    public void start() {

        listenerThread = new Thread(() -> {

            Selector clientsSelector = connectionsRegister.getClientsSelector();

            while (!stopped && clientsSelector.isOpen()) {

                try {

                    Logger.debug("[ClientConnectionsListener] Waiting for client's data ... ");

                    int selected = clientsSelector.select();

                    if (!clientsSelector.isOpen()) {
                        Logger.log("[ClientConnectionsListener] Quiting ...");
                        return;
                    }

                    if (selected == 0) {
                        continue;
                    }

                    Set<SelectionKey> selectedKeys = clientsSelector.selectedKeys();

                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    Logger.debug("[ClientConnectionsListener] Keys selected=[" + selectedKeys.size() + "]");

                    while (keyIterator.hasNext()) {

                        SelectionKey key = keyIterator.next();

                        if (key.isReadable()) {
                            ClientConnection clientConnection = (ClientConnection) key.attachment();
                            if (clientConnection.isAlive()) {
                                Logger.debug("[ClientConnectionsListener] Reading ... ");
                                ConnectionMessage message = clientConnection.readMessage();
                                Logger.debug("[ClientConnectionsListener] Read: " + message.getDataAsString());
                                messageReadingQueue.enqueue(message);
                            }
                        }

                        keyIterator.remove();
                    }

                } catch (Exception e) {
                    Logger.debug("[ClientConnectionsListener][ERROR], "
                        + "type=[" + e.getClass().getName() + "], "
                        + "message=[" + e.getMessage() + "]");
                }
            }
        }, "ClientConnectionsListener");

        listenerThread.start();
    }

    public void stop() throws InterruptedException {
        stopped = true;

        try {
            connectionsRegister.getClientsSelector().close();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }

        listenerThread.join();

        Logger.log("Client connections listener: stopped");
    }
}
