package ez.messaging.handlers;

import ez.connection.client.ClientsRegistry;
import ez.connection.data.ConnectionMessage;
import ez.messaging.data.transport.Message;

public class HelloMessageHandler implements MessageHandler {

    private ClientsRegistry clientsRegistry;

    public HelloMessageHandler(ClientsRegistry clientsRegistry) {
        this.clientsRegistry = clientsRegistry;
    }

    @Override
    public void handleMessage(Message message, ConnectionMessage connectionMessage) {

        // TODO authenticate connection based on HelloMessagePayload
        clientsRegistry.registerConnection(message.getSenderId(), connectionMessage.getConnection());
    }
}
