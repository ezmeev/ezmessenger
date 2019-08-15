package ez.messaging.handlers;

import ez.connection.registry.ConnectionsRegistry;
import ez.connection.data.ConnectionMessage;
import ez.messaging.data.transport.Message;

public class HelloMessageHandler implements MessageHandler {

    private ConnectionsRegistry connectionsRegistry;

    public HelloMessageHandler(ConnectionsRegistry connectionsRegistry) {
        this.connectionsRegistry = connectionsRegistry;
    }

    @Override
    public void handleMessage(Message message, ConnectionMessage connectionMessage) {

        // TODO authenticate connection based on HelloMessagePayload
        connectionsRegistry.registerConnection(message.getSenderId(), connectionMessage.getConnection());
    }
}
