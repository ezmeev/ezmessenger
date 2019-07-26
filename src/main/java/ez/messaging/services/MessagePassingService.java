package ez.messaging.services;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import ez.connection.client.ClientConnection;
import ez.connection.client.ClientsRegistry;
import ez.connection.data.ConnectionMessage;
import ez.messaging.data.User;
import ez.messaging.data.transport.Message;
import ez.util.JsonConvert;
import ez.util.Logger;

public class MessagePassingService {

    private ClientsRegistry connections;

    public MessagePassingService(ClientsRegistry connections) {

        this.connections = connections;
    }

    public void tryAcknowledgeMessage(User sender, Message message) {
        try {
            String senderIdentity = sender.getIdentity();
            sendMessage(senderIdentity, Message.createAckByServerMessage(senderIdentity, message.getMessageId()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // TODO
        }
    }

    public void sendMessageTo(User receiver, Message message) {
        try {
            sendMessage(receiver.getIdentity(), message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // TODO
        }
    }

    private void sendMessage(String identity, Message message) throws JsonProcessingException {
        String payload = JsonConvert.serialize(message);
        ConnectionMessage connectionMessage = new ConnectionMessage(payload.getBytes(StandardCharsets.UTF_8));

        Map<String, ClientConnection> clients = connections.getSnapshot();
        if (clients.containsKey(identity)) {
            ClientConnection clientConnection = clients.get(identity);
            clientConnection.sendMessage(connectionMessage);
        } else {
            Logger.log("Receiver [" + identity + "] not connected");
            // client not found amongst connected, that might happen when:
            // 1. client is not connected - then we fine, messages will be delivered next when client connected
            // 2. client connected to another instance - then we have to pass this message there (not implemented yet)
        }
    }
}
