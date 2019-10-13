package ez.connection.registry;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ez.connection.client.ClientConnection;
import ez.util.Logger;

public class ConnectionsRegistry {

    private Map<String, ClientConnection> clientConnections;

    private Selector clientsSelector;

    // FIXME it's confusing to have following two methods here
    // as they are from different "level" and not really concerned about connection, they are more about clients
    public void registerConnection(String identity, ClientConnection clientConnection) {
        clientConnections.put(identity, clientConnection);
    }

    public void unregisterConnection(String identity) {
        clientConnections.remove(identity);
    }

    public Map<String, ClientConnection> getSnapshot() {
        return new ConcurrentHashMap<>(clientConnections);
    }

    /////

    public void registerChannel(ClientConnection connection) throws IOException {
        Logger.log("[ClientsRegistry] Registering connection");

        SocketChannel clientChannel = connection.getChannel();
        clientChannel.configureBlocking(false);
        clientChannel.register(clientsSelector, SelectionKey.OP_READ, connection);

        clientsSelector.wakeup();
    }

    public void unregisterChannel(ClientConnection connection) {
        try {
            connection.getChannel().close();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }

    public Selector getClientsSelector() {
        return clientsSelector;
    }

    public void init() {
        this.clientConnections = new ConcurrentHashMap<>();
        try {
            this.clientsSelector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException("Unable to open clients selector", e);
        }
    }
}
