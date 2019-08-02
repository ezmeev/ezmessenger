package ez.connection.client;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import ez.connection.data.ConnectionMessage;
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

            Selector clientsSelector = connectionsRegister.getClientsSelector();

            while (!stopped && clientsSelector.isOpen()) {

                try {

                    Logger.log("[ClientConnectionsListener] Waiting for client's data ... ");

                    clientsSelector.select();

                    if (!clientsSelector.isOpen()) {
                        Logger.log("[ClientConnectionsListener] Quiting ...");
                        return;
                    }

                    Set<SelectionKey> selectedKeys = clientsSelector.selectedKeys();

                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while (keyIterator.hasNext()) {

                        SelectionKey key = keyIterator.next();

                        if (key.isReadable()) {

                            ClientConnection clientConnection = (ClientConnection) key.attachment();

                            ConnectionMessage connectionMessage = clientConnection.readMessage();

                            messageReadingQueue.enqueue(connectionMessage);
                        }

                        keyIterator.remove();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    // TODO
                }
            }
        });

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
