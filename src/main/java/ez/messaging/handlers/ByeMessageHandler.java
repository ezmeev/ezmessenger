package ez.messaging.handlers;

import ez.connection.data.ConnectionMessage;
import ez.connection.registry.ConnectionsRegistry;
import ez.messaging.data.transport.Message;

public class ByeMessageHandler implements MessageHandler {

    private ConnectionsRegistry connectionsRegistry;

    public ByeMessageHandler(ConnectionsRegistry connectionsRegistry) {
        this.connectionsRegistry = connectionsRegistry;
    }

    @Override
    public void handleMessage(Message message, ConnectionMessage connectionMessage) {

        connectionsRegistry.unregisterChannel(connectionMessage.getConnection());
    }
}
